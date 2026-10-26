package me.itzisonn_.meazy.parser.cfg.helper

import me.itzisonn_.meazy.parser.cfg.ControlFlowBlock
import me.itzisonn_.meazy.parser.cfg.ControlFlowGraph

object MissingReturnAnalyzer {
    fun hasMissingReturn(controlFlowGraph: ControlFlowGraph): Boolean {
        val reachable = linkedSetOf<ControlFlowBlock>()
        val worklist = ArrayDeque<ControlFlowBlock>()

        worklist.addLast(controlFlowGraph.entry)

        while (worklist.isNotEmpty()) {
            val block = worklist.removeLast()
            if (!reachable.add(block)) continue
            block.successors.forEach(worklist::addLast)
        }

        return controlFlowGraph.exit in reachable
    }
}