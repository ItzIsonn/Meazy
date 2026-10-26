package me.itzisonn_.meazy.parser.cfg

import me.itzisonn_.meazy.parser.ast.statement.BaseCallStatement

class ConstructorAnalyzer(private val cfg: Cfg) {
    private val reachable = linkedSetOf<BasicBlock>()
    private val inState = mutableMapOf<BasicBlock, Boolean>()
    private val outState = mutableMapOf<BasicBlock, Boolean>()
    private val errors = mutableListOf<String>()

    fun analyze(): List<String> {
        findReachableBlocks()
        initializeStates()
        solveDataFlow()
        checkReturns()
        checkFallThrough()
        return errors
    }

    private fun findReachableBlocks() {
        val worklist = ArrayDeque<BasicBlock>()
        worklist.addLast(cfg.entry)

        while (worklist.isNotEmpty()) {
            val block = worklist.removeLast()
            if (!reachable.add(block)) continue

            block.successors.forEach { successor ->
                worklist.addLast(successor)
            }
        }
    }

    private fun initializeStates() {
        for (block in reachable) {
            inState[block] = true
            outState[block] = true
        }

        inState[cfg.entry] = false
        outState[cfg.entry] = false
    }

    private fun solveDataFlow() {
        var changed: Boolean

        do {
            changed = false

            for (block in cfg.blocks) {
                if (block !in reachable) continue
                val newIn = calculateInState(block)
                val newOut = transfer(block, newIn)

                if (newIn != inState[block] || newOut != outState[block]) {
                    inState[block] = newIn
                    outState[block] = newOut
                    changed = true
                }
            }
        }
        while (changed)
    }

    private fun calculateInState(block: BasicBlock): Boolean {
        if (block == cfg.entry) {
            return false
        }

        var result = true

        for (predecessor in block.predecessors) {
            if (predecessor !in reachable) continue
            result = result && outState.getValue(predecessor)
        }

        return result
    }

    private fun transfer(block: BasicBlock, incoming: Boolean): Boolean {
        var initialized = incoming

        for (statement in block.statements) {
            when (statement) {
                is BaseCallStatement -> {
                    initialized = true
                }

                /*is ThisCallStmt -> { TODO
                    initialized = true
                }*/
            }
        }

        return initialized
    }

    private fun checkReturns() {
        for (block in reachable) {
            if (block.terminator is Terminator.Return) {
                val initialized = outState.getValue(block)

                if (!initialized) {
                    errors.add("Constructor returns before calling base() at $block")
                }
            }
        }
    }

    private fun checkFallThrough() {
        if (cfg.exit !in reachable) return
        val initialized = inState.getValue(cfg.exit)

        if (!initialized) {
            errors.add("Constructor reaches its end without calling base()")
        }
    }
}