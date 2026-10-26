package me.itzisonn_.meazy.parser.cfg

import me.itzisonn_.meazy.parser.ast.statement.*

class ControlFlowGraphBuilder {
    private val controlFlowGraph = ControlFlowGraph()
    private val loops = ArrayDeque<LoopContext>()

    fun build(body: List<Statement>): ControlFlowGraph {
        val exits = buildStatements(body, mutableSetOf(controlFlowGraph.entry))

        for (block in exits) {
            block.connectTo(controlFlowGraph.exit)
        }

        return controlFlowGraph
    }

    private fun buildStatements(statements: List<Statement>, incoming: Set<ControlFlowBlock>): Set<ControlFlowBlock> {
        var current = incoming

        for (statement in statements) {
            current = buildStatement(statement, current)
        }

        return current
    }

    private fun buildStatement(statement: Statement, incoming: Set<ControlFlowBlock>): Set<ControlFlowBlock> {
        if (incoming.isEmpty()) {
            return setOf()
        }

        return when (statement) {
            is IfStatement -> buildIf(statement, incoming)
            is ForeachStatement -> buildForeach(statement, incoming)
            is WhileStatement -> buildWhile(statement, incoming)

            is BreakStatement -> {
                val block = controlFlowGraph.newBlock()
                connect(incoming, block)

                val context = currentLoop()
                block.terminator = Terminator.Jump(context.breakTarget)
                block.connectTo(context.breakTarget)

                setOf()
            }

            is ContinueStatement -> {
                val block = controlFlowGraph.newBlock()
                connect(incoming, block)

                val context = currentLoop()
                block.terminator = Terminator.Jump(context.continueTarget)
                block.connectTo(context.continueTarget)

                setOf()
            }

            is ReturnStatement -> {
                val block = controlFlowGraph.newBlock()
                connect(incoming, block)

                block.terminator = Terminator.Return(statement.value)
                setOf()
            }

            else -> {
                val block = controlFlowGraph.newBlock()
                connect(incoming, block)

                block.addStatement(statement)
                setOf(block)
            }
        }
    }



    private fun buildIf(
        statement: IfStatement,
        incoming: Set<ControlFlowBlock>
    ): Set<ControlFlowBlock> {
        if (incoming.isEmpty()) return emptySet()

        val mergeBlock = controlFlowGraph.newBlock()
        var currentIncoming = incoming

        for (case in statement.cases) {
            val condition = case.condition

            if (condition == null) {
                val elseExits = buildStatements(case.body, currentIncoming)
                connect(elseExits, mergeBlock)

                currentIncoming = emptySet()
                break
            }

            val conditionBlock = controlFlowGraph.newBlock()
            val bodyBlock = controlFlowGraph.newBlock()
            val nextConditionBlock = controlFlowGraph.newBlock()

            connect(currentIncoming, conditionBlock)
            conditionBlock.terminator = Terminator.ConditionalJump(
                TerminatorCondition.ExpressionCondition(condition),
                bodyBlock,
                nextConditionBlock
            )

            conditionBlock.connectTo(bodyBlock)
            conditionBlock.connectTo(nextConditionBlock)

            val bodyExits = buildStatements(case.body, setOf(bodyBlock))
            connect(bodyExits, mergeBlock)

            currentIncoming = setOf(nextConditionBlock)
        }

        if (currentIncoming.isNotEmpty()) connect(currentIncoming, mergeBlock)
        return setOf(mergeBlock)
    }

    private fun buildForeach(
        statement: ForeachStatement,
        incoming: Set<ControlFlowBlock>
    ): Set<ControlFlowBlock> {
        val iteratorBlock = controlFlowGraph.newBlock()
        val bodyBlock = controlFlowGraph.newBlock()
        val afterLoopBlock = controlFlowGraph.newBlock()

        connect(incoming, iteratorBlock)

        iteratorBlock.terminator = Terminator.ConditionalJump(
            TerminatorCondition.ForeachHasNext(
                statement.collection,
                statement.id,
                statement.dataType
            ),
            bodyBlock,
            afterLoopBlock
        )

        iteratorBlock.connectTo(bodyBlock)
        iteratorBlock.connectTo(afterLoopBlock)

        loops.addLast(LoopContext(afterLoopBlock, iteratorBlock))

        val bodyExits = try {
            buildStatements(statement.body, setOf(bodyBlock))
        }
        finally {
            loops.removeLast()
        }

        connect(bodyExits, iteratorBlock)
        return setOf(afterLoopBlock)
    }

    private fun buildWhile(
        statement: WhileStatement,
        incoming: Set<ControlFlowBlock>
    ): Set<ControlFlowBlock> {
        val conditionBlock = controlFlowGraph.newBlock()
        val bodyBlock = controlFlowGraph.newBlock()
        val afterLoopBlock = controlFlowGraph.newBlock()

        connect(incoming, conditionBlock)

        conditionBlock.terminator = Terminator.ConditionalJump(
            TerminatorCondition.ExpressionCondition(statement.condition),
            bodyBlock, afterLoopBlock
        )
        conditionBlock.connectTo(bodyBlock)
        conditionBlock.connectTo(afterLoopBlock)

        loops.addLast(LoopContext(afterLoopBlock, conditionBlock))
        val bodyExits = buildStatements(statement.body, setOf(bodyBlock))
        loops.removeLast()

        connect(bodyExits, conditionBlock)
        return setOf(afterLoopBlock)
    }



    private fun connect(blocks: Set<ControlFlowBlock>, to: ControlFlowBlock) {
        for (block in blocks) {
            block.connectTo(to)
        }
    }

    private fun currentLoop(): LoopContext {
        check(loops.isNotEmpty()) { "Break or Continue statement is used outside loop" }
        return loops.last()
    }
}



private class LoopContext(
    val breakTarget: ControlFlowBlock,
    val continueTarget: ControlFlowBlock
)