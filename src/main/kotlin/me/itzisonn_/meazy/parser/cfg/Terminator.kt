package me.itzisonn_.meazy.parser.cfg

import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.data.DataType

sealed interface Terminator {
    data class Jump(
        val target: BasicBlock
    ) : Terminator

    data class ConditionalJump(
        val condition: TerminatorCondition,
        val trueTarget: BasicBlock,
        val falseTarget: BasicBlock
    ) : Terminator

    data class Return(
        val expression: Expression?
    ) : Terminator
}



sealed interface TerminatorCondition {
    data class ExpressionCondition(
        val expression: Expression
    ) : TerminatorCondition

    data class ForeachHasNext(
        val collection: Expression,
        val variableId: String,
        val dataType: DataType
    ) : TerminatorCondition
}