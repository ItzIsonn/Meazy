package me.itzisonn_.meazy.parser.ast.expression

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement
import me.itzisonn_.meazy.runtime.environment.Environment

class MemberExpression(
    val receiver: Expression,
    val member: Expression,
    val isNullSafe: Boolean
) : Expression, LocalStatement {
    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        member.emit(instructions, environment, this)
    }

    override fun getType(environment: Environment, parent: ProgramUnit): DataType {
        return member.getType(environment, this)
    }

    override fun alwaysReturns() = false
}