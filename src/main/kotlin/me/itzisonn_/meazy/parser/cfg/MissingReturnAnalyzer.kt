package me.itzisonn_.meazy.parser.cfg

class MissingReturnAnalyzer(private val cfg: Cfg) {
    fun hasMissingReturn(): Boolean {
        val reachable = findReachableBlocks()
        return cfg.exit in reachable
    }

    private fun findReachableBlocks(): Set<BasicBlock> {
        val reachable = linkedSetOf<BasicBlock>()
        val worklist = ArrayDeque<BasicBlock>()

        worklist.addLast(cfg.entry)

        while (worklist.isNotEmpty()) {
            val block = worklist.removeLast()
            if (!reachable.add(block)) continue
            block.successors.forEach(worklist::addLast)
        }

        return reachable
    }
}