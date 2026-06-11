package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.MemberExpression
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier
import me.itzisonn_.meazy.parser.ast.expression.literal.ThisLiteral
import me.itzisonn_.meazy.parser.modifier.Modifiers
import me.itzisonn_.meazy.runtime.VariableValue
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils.getClass
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils.getVariable
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils.isInstanceOf
import me.itzisonn_.meazy.runtime.environment.FileEnvironment
import me.itzisonn_.meazy.util.MiscUtils.convertPrimitiveOrBoxed
import java.lang.constant.ClassDesc

class AssignmentStatement(val id: Expression, val value: Expression) : LocalStatement {
    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        val resolvedVariable = resolveVariable(environment, parent)
        if (resolvedVariable.isConstant) throw RuntimeException("Can't reassign constant variable " + resolvedVariable.id + " TODO")

        val variableType = resolvedVariable.type
        val valueType = value.getType(environment, this).classDesc

        if (resolvedVariable.classDesc != null && resolvedVariable.target != null) {
            resolvedVariable.target.emit(instructions, environment, this)
        }

        value.emit(instructions, environment, this)

        if (!isInstanceOf(environment, valueType, variableType)) {
            if (!convertPrimitiveOrBoxed(instructions, valueType, variableType)) {
                throw RuntimeException("Can't assign value of type $valueType to variable with type $variableType")
            }
        }

        if (resolvedVariable.classDesc == null) {
            instructions.storeLocal(resolvedVariable.type, resolvedVariable.slot)
        }
        else if (resolvedVariable.target == null) {
            instructions.storeStaticField(resolvedVariable.classDesc, resolvedVariable.id, resolvedVariable.type)
        }
        else {
            instructions.storeField(resolvedVariable.classDesc, resolvedVariable.id, resolvedVariable.type)
        }
    }

    override fun alwaysReturns() = false



    private fun resolveVariable(environment: Environment, parent: ProgramUnit): ResolvedVariable {
        val variableValue = resolveMeazyVariable(environment) ?: error("Can't find variable")

        val className = when (val parent = variableValue.parentEnvironment) {
            is ClassEnvironment -> parent.id
            is FileEnvironment -> parent.packageName
            else -> null
        }

        val target = if (parent is MemberExpression) {
            if (parent.receiver is ClassIdentifier) null else parent.receiver
        }
        else if (variableValue.modifiers.contains(Modifiers.SHARED()) || variableValue.parentEnvironment is FileEnvironment) {
            null
        }
        else {
            ThisLiteral()
        }

        return ResolvedVariable(
            if (className == null) null else ClassDesc.of(className),
            if (className == null) variableValue.slot else -1,
            variableValue.isConstant,
            variableValue.id!!,
            variableValue.dataType.classDesc,
            target
        )
    }

    private fun resolveMeazyVariable(environment: Environment): VariableValue? {
        if (id is MemberExpression) {
            if (id.member !is VariableIdentifier) {
                throw RuntimeException("Cant assign value to not variable TODO")
            }

            val classDesc: ClassDesc = id.receiver.getType(environment, this).classDesc
            val classEnvironment = getClass(environment, classDesc).orElse(null) ?: return null

            return classEnvironment.getVariable(id.member.id).orElse(null)
        }

        if (id !is VariableIdentifier) {
            throw RuntimeException("Cant assign value to not variable TODO")
        }

        return getVariable(environment, id.id).orElse(null)
    }

    private class ResolvedVariable(
        val classDesc: ClassDesc?,
        val slot: Int,
        val isConstant: Boolean,
        val id: String,
        val type: ClassDesc,
        val target: Expression?
    )
}
