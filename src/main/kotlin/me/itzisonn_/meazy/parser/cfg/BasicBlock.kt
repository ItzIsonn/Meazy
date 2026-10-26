package me.itzisonn_.meazy.parser.cfg

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

    override fun toString() = "Block#$id"
}