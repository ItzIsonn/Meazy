package me.itzisonn_.meazy.parser.ast

import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.Identifier
import me.itzisonn_.meazy.parser.ast.expression.MemberExpression
import me.itzisonn_.meazy.parser.ast.expression.literal.ThisLiteral
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.data.symbol.VariableSymbol
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.FileEnvironment
import me.itzisonn_.meazy.runtime.environment.getClass
import me.itzisonn_.meazy.runtime.environment.getVariable
import java.lang.constant.ClassDesc

object SymbolResolver {
    context(parents: ParentMap)
    fun Environment.resolveVariable(target: ProgramUnit): ResolvedVariable {
        val variableValue = resolveVariableSymbol(target) ?: error("Can't find variable")
        val className = variableValue.parentEnvironment.fullClassName

        val receiver = if (Modifiers.shared in variableValue.modifiers || variableValue.parentEnvironment is FileEnvironment) {
            null
        }
        else {
            val memberExpression = getMemberExpression(target)
            memberExpression?.receiver ?: ThisLiteral()
        }

        return ResolvedVariable(
            if (className == null) null else ClassDesc.of(className),
            if (className == null) variableValue.slot else -1,
            variableValue.isConstant,
            variableValue.id!!,
            variableValue.dataType.classDesc,
            variableValue.dataType.isNullable,
            receiver
        )
    }

    context(parents: ParentMap)
    private fun Environment.resolveVariableSymbol(target: ProgramUnit): VariableSymbol? {
        val memberExpression = getMemberExpression(target)
        if (memberExpression != null) {
            if (memberExpression.member !is Identifier) {
                error("Can't assign value to not variable TODO")
            }

            val classDesc = memberExpression.receiver.getType(this).classDesc
            val cls = getClass(classDesc) ?: return null
            return cls.environment.getVariable(memberExpression.member.id)
        }

        if (target is Identifier) {
            return getVariable(target.id)
        }

        error("Invalid target to resolve variable")
    }

    context(parents: ParentMap)
    private fun getMemberExpression(target: ProgramUnit): MemberExpression? {
        if (target is MemberExpression) {
            return target
        }

        val parent = target.parent
        if (parent is MemberExpression && parent.member == target) {
            return parent
        }

        return null
    }

    class ResolvedVariable(
        val classDesc: ClassDesc?,
        val slot: Int,
        val isConstant: Boolean,
        val id: String,
        val type: ClassDesc,
        val isNullable: Boolean,
        val receiver: Expression?
    )
}