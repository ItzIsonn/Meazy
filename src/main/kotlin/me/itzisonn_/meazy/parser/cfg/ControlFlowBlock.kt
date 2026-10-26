package me.itzisonn_.meazy.parser.cfg

import me.itzisonn_.meazy.parser.ast.statement.Statement

data class ControlFlowBlock(val id: Int) {
    private val _statements = mutableListOf<Statement>()
    val statements get() = _statements.toList()

    var terminator: Terminator? = null

    private val _successors = linkedSetOf<ControlFlowBlock>()
    val successors get() = _successors.toList()

    private val _predecessors = linkedSetOf<ControlFlowBlock>()
    val predecessors get() = _predecessors.toList()

    fun addStatement(statement: Statement) {
        _statements.add(statement)
    }

    fun connectTo(target: ControlFlowBlock) {
        _successors.add(target)
        target._predecessors.add(this)
    }

    override fun toString() = "Block#$id"
}