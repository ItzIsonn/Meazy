package me.itzisonn_.meazy.parser.ast.expression.identifier

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.MemberExpression
import me.itzisonn_.meazy.parser.ast.expression.literal.ThisLiteral
import me.itzisonn_.meazy.parser.modifier.Modifiers
import me.itzisonn_.meazy.runtime.VariableValue
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.FileEnvironment
import me.itzisonn_.meazy.runtime.environment.getClass
import me.itzisonn_.meazy.runtime.environment.getVariable
import java.lang.constant.ClassDesc
import kotlin.uuid.Uuid

class VariableIdentifier(id: String) : Identifier(id) {
    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        val resolvedVariable = resolveVariable(environment, parent)

        if (resolvedVariable.classDesc == null) {
            instructions.getLocal(resolvedVariable.type, resolvedVariable.slot)
        }
        else if (resolvedVariable.target == null) {
            instructions.getStaticField(resolvedVariable.classDesc, resolvedVariable.id, resolvedVariable.type)
        }
        else {
            resolvedVariable.target.emit(instructions, environment, this)
            var endLabel: Uuid? = null

            if (parent is MemberExpression) {
                if (!parent.isNullSafe) {
                    if (resolvedVariable.target.getType(environment, this).isNullable) {
                        throw RuntimeException("Unsafe member call of function $id on object of type ${resolvedVariable.classDesc.descriptorString()}")
                    }
                }
                else {
                    val nonnullLabel = instructions.createAndInitLabel()
                    endLabel = instructions.createAndInitLabel()

                    instructions.duplicate()
                    instructions.gotoLabelIfNonNull(nonnullLabel)

                    instructions.pop()
                    instructions.loadNull()
                    instructions.gotoLabel(endLabel)

                    instructions.bindLabel(nonnullLabel)
                }
            }

            instructions.getField(resolvedVariable.classDesc, resolvedVariable.id, resolvedVariable.type)

            if (endLabel != null) {
                instructions.bindLabel(endLabel)
            }
        }
    }

    override fun getType(environment: Environment, parent: ProgramUnit): DataType {
        val resolvedVariable = resolveVariable(environment, parent)
        return DataType.of(resolvedVariable.type, resolvedVariable.isNullable)
    }

    private fun resolveVariable(environment: Environment, parent: ProgramUnit): ResolvedVariable {
        val variableValue = resolveMeazyVariable(environment, parent) ?: error("Can't find variable $id")

        val className = variableValue.parentEnvironment.fullClassName
        val target = if (parent is MemberExpression) {
            if (parent.receiver is ClassIdentifier) null else parent.receiver
        }
        else if (Modifiers.shared in variableValue.modifiers || variableValue.parentEnvironment is FileEnvironment) {
            null
        }
        else {
            ThisLiteral()
        }

        return ResolvedVariable(
            if (className == null) null else ClassDesc.of(className),
            if (className == null) variableValue.slot else -1,
            variableValue.id!!,
            variableValue.dataType.classDesc,
            variableValue.dataType.isNullable,
            target
        )
    }

    private fun resolveMeazyVariable(environment: Environment, parent: ProgramUnit): VariableValue? {
        if (parent is MemberExpression) {
            val dataType = parent.receiver.getType(environment, this)
            val classDesc = dataType.classDesc

            val classEnvironment = environment.getClass(classDesc) ?: return null
            return classEnvironment.getVariable(id).orElse(null)
        }

        return environment.getVariable(id)
    }

    private class ResolvedVariable(
        val classDesc: ClassDesc?,
        val slot: Int,
        val id: String,
        val type: ClassDesc,
        val isNullable: Boolean,
        val target: Expression?
    )
}