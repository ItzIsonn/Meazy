package me.itzisonn_.meazy.parser.cfg.helper

import me.itzisonn_.meazy.parser.ast.statement.BaseCallStatement
import me.itzisonn_.meazy.parser.cfg.ControlFlowBlock
import me.itzisonn_.meazy.parser.cfg.ControlFlowGraph
import me.itzisonn_.meazy.parser.cfg.Terminator

class ConstructorAnalyzer(private val controlFlowGraph: ControlFlowGraph) {
    private val reachable = linkedSetOf<ControlFlowBlock>()
    private val inState = mutableMapOf<ControlFlowBlock, Boolean>()
    private val outState = mutableMapOf<ControlFlowBlock, Boolean>()

    fun analyze() {
        findReachableBlocks()
        initializeStates()
        solveDataFlow()
        checkReturns()
        checkFallThrough()
    }

    private fun findReachableBlocks() {
        val worklist = ArrayDeque<ControlFlowBlock>()
        worklist.addLast(controlFlowGraph.entry)

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

        inState[controlFlowGraph.entry] = false
        outState[controlFlowGraph.entry] = false
    }

    private fun solveDataFlow() {
        var changed: Boolean

        do {
            changed = false

            for (block in controlFlowGraph.blocks) {
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

    private fun calculateInState(block: ControlFlowBlock): Boolean {
        if (block == controlFlowGraph.entry) {
            return false
        }

        var result = true

        for (predecessor in block.predecessors) {
            if (predecessor !in reachable) continue
            result = result && outState.getValue(predecessor)
        }

        return result
    }

    private fun transfer(block: ControlFlowBlock, incoming: Boolean): Boolean {
        var initialized = incoming

        for (statement in block.statements) {
            when (statement) {
                is BaseCallStatement -> {
                    initialized = true
                }

                /*is ThisCallStatement -> { TODO
                    initialized = true
                }*/
            }
        }

        return initialized
    }

    private fun checkReturns() {
        for (block in reachable) {
            if (block.terminator !is Terminator.Return) continue

            val initialized = outState.getValue(block)
            if (!initialized) error("Constructor returns before calling base() at $block")
        }
    }

    private fun checkFallThrough() {
        if (controlFlowGraph.exit !in reachable) return

        val initialized = inState.getValue(controlFlowGraph.exit)
        if (!initialized) error("Constructor reaches its end without calling base()")
    }
}