package me.itzisonn_.meazy.parser.ast.expression

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.expression.literal.ThisLiteral
import me.itzisonn_.meazy.parser.ast.parent
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.data.VariableValue
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.FileEnvironment
import me.itzisonn_.meazy.runtime.environment.getClass
import me.itzisonn_.meazy.runtime.environment.getVariable
import me.itzisonn_.meazy.runtime.environment.resolveClassDesc
import java.lang.constant.ClassDesc
import kotlin.uuid.Uuid

class Identifier(val id: String) : Expression {
    override val children = setOf<ProgramUnit>()

    context(parents: ParentMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        val resolvedVariable = resolveVariable(environment)

        if (resolvedVariable.classDesc == null) {
            instructions.getLocal(resolvedVariable.type, resolvedVariable.slot)
        }
        else if (resolvedVariable.target == null) {
            instructions.getStaticField(resolvedVariable.classDesc, resolvedVariable.id, resolvedVariable.type)
        }
        else {
            resolvedVariable.target.emit(instructions, environment)
            var endLabel: Uuid? = null

            val parent = parent
            if (parent is MemberExpression) {
                if (!parent.isNullSafe) {
                    if (resolvedVariable.target.getType(environment).isNullable) {
                        throw RuntimeException("Unsafe member access of variable '$id' on object of type ${resolvedVariable.classDesc.descriptorString()}")
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

    context(parents: ParentMap)
    override fun getType(environment: Environment): DataType {
        val parent = parent

        if (parent is MemberExpression && this != parent.member) {
            val classDesc = environment.resolveClassDesc(id, false)

            if (environment.getClass(classDesc) != null) {
                return DataType.ofNonNull(classDesc)
            }
        }

        val resolvedVariable = resolveVariable(environment)
        return DataType.of(resolvedVariable.type, resolvedVariable.isNullable)
    }

    context(parents: ParentMap)
    private fun resolveVariable(environment: Environment): ResolvedVariable {
        val parent = parent
        val variableValue = resolveMeazyVariable(environment) ?: error("Can't find variable $id")

        val className = variableValue.parentEnvironment.fullClassName
        val target = if (Modifiers.shared in variableValue.modifiers || variableValue.parentEnvironment is FileEnvironment) {
            null
        }
        else if (parent is MemberExpression) parent.receiver
        else ThisLiteral()

        return ResolvedVariable(
            if (className == null) null else ClassDesc.of(className),
            if (className == null) variableValue.slot else -1,
            variableValue.id!!,
            variableValue.dataType.classDesc,
            variableValue.dataType.isNullable,
            target
        )
    }

    context(parents: ParentMap)
    private fun resolveMeazyVariable(environment: Environment): VariableValue? {
        val parent = parent

        if (parent is MemberExpression && this == parent.member) {
            val dataType = parent.receiver.getType(environment)
            val classDesc = dataType.classDesc

            val classEnvironment = environment.getClass(classDesc) ?: return null
            return classEnvironment.getVariable(id)
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