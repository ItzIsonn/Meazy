package me.itzisonn_.meazy.parser.cfg

import me.itzisonn_.meazy.parser.ast.statement.Statement

class ControlFlowGraph {
    val entry: ControlFlowBlock
    val exit: ControlFlowBlock

    private val _blocks = mutableListOf<ControlFlowBlock>()
    val blocks get() = _blocks.toList()

    private var nextId = 0

    init {
        entry = newBlock()
        exit = newBlock()
    }

    fun newBlock(): ControlFlowBlock {
        val block = ControlFlowBlock(nextId++)
        _blocks.add(block)
        return block
    }

    companion object {
        fun build(body: List<Statement>) = ControlFlowGraphBuilder().build(body)
    }
}