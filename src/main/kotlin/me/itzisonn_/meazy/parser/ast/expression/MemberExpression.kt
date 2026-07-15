package me.itzisonn_.meazy.parser.ast.expression

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement
import me.itzisonn_.meazy.runtime.environment.Environment

class MemberExpression(
    val receiver: Expression,
    val member: Expression,
    val isNullSafe: Boolean
) : Expression, LocalStatement {
    override val children = setOf(receiver, member)

    context(parents: ParentMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        member.emit(instructions, environment)
    }

    context(parents: ParentMap)
    override fun getType(environment: Environment): DataType {
        return member.getType(environment)
    }

    override fun alwaysReturns() = false
}