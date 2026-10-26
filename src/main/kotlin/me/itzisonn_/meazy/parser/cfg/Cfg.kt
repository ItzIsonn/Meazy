package me.itzisonn_.meazy.parser.cfg

class Cfg {
    val entry: BasicBlock
    val exit: BasicBlock
    val blocks = mutableListOf<BasicBlock>()

    private var nextId = 0

    init {
        entry = newBlock()
        exit = newBlock()
    }

    fun newBlock(): BasicBlock {
        val block = BasicBlock(nextId++)
        blocks.add(block)
        return block
    }
}