package me.itzisonn_.meazy.parser.cfg

import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.statement.Statement

class BasicBlock(val id: Int) {
    val statements = mutableListOf<Statement>()
    var terminator: Terminator? = null

    val successors = linkedSetOf<BasicBlock>()
    val predecessors = linkedSetOf<BasicBlock>()

    fun connectTo(target: BasicBlock) {
        successors.add(target)
        target.predecessors.add(this)
    }

    override fun toString() = "B$id"
}



sealed interface Terminator {
    data class Jump(
        val target: BasicBlock
    ) : Terminator

    data class ConditionalJump(
        val condition: Expression,
        val trueTarget: BasicBlock,
        val falseTarget: BasicBlock
    ) : Terminator

    data class Return(
        val expression: Expression?
    ) : Terminator
}