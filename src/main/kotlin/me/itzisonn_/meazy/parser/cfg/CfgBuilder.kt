package me.itzisonn_.meazy.parser.cfg

import me.itzisonn_.meazy.parser.ast.statement.*

class CfgBuilder {
    private val cfg = Cfg()
    private val loops = ArrayDeque<LoopContext>()

    fun build(body: List<Statement>): Cfg {
        val exits = buildStatements(
            body,
            mutableSetOf(cfg.entry)
        )

        for (block in exits) {
            block.connectTo(cfg.exit)
        }

        return cfg
    }

    private fun buildStatements(statements: List<Statement>, incoming: Set<BasicBlock>): Set<BasicBlock> {
        var current = incoming

        for (statement in statements) {
            current = buildStatement(statement, current)
        }

        return current
    }

    private fun buildStatement(statement: Statement, incoming: Set<BasicBlock>): Set<BasicBlock> {
        if (incoming.isEmpty()) {
            return setOf()
        }

        return when (statement) {
            is IfStatement -> buildIf(statement, incoming)
            is ForeachStatement -> buildForeach(statement, incoming)
            is WhileStatement -> buildWhile(statement, incoming)

            is BreakStatement -> {
                val block = newBlock()
                connect(incoming, block)

                val context = currentLoop()
                block.terminator = Terminator.Jump(context.breakTarget)
                block.connectTo(context.breakTarget)

                setOf()
            }

            is ContinueStatement -> {
                val block = newBlock()
                connect(incoming, block)

                val context = currentLoop()
                block.terminator = Terminator.Jump(context.continueTarget)
                block.connectTo(context.continueTarget)

                setOf()
            }

            is ReturnStatement -> {
                val block = newBlock()
                connect(incoming, block)

                block.terminator = Terminator.Return(statement.value)
                setOf()
            }

            else -> {
                val block = newBlock()
                connect(incoming, block)

                block.statements.add(statement)
                setOf(block)
            }
        }
    }



    private fun buildIf(
        statement: IfStatement,
        incoming: Set<BasicBlock>
    ): Set<BasicBlock> {
        if (incoming.isEmpty()) return emptySet()

        val mergeBlock = newBlock()
        var currentIncoming = incoming

        for (case in statement.cases) {
            val condition = case.condition

            if (condition == null) {
                val elseExits = buildStatements(case.body, currentIncoming)
                connect(elseExits, mergeBlock)

                currentIncoming = emptySet()
                break
            }

            val conditionBlock = newBlock()
            val bodyBlock = newBlock()
            val nextConditionBlock = newBlock()

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
        incoming: Set<BasicBlock>
    ): Set<BasicBlock> {
        val iteratorBlock = newBlock()
        val bodyBlock = newBlock()
        val afterLoopBlock = newBlock()

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
        incoming: Set<BasicBlock>
    ): Set<BasicBlock> {
        val conditionBlock = newBlock()
        val bodyBlock = newBlock()
        val afterLoopBlock = newBlock()

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



    private fun newBlock(): BasicBlock {
        return cfg.newBlock()
    }

    private fun connect(from: Set<BasicBlock>, to: BasicBlock) {
        for (block in from) {
            block.connectTo(to)
        }
    }

    private fun currentLoop(): LoopContext {
        check(loops.isNotEmpty()) { "Break or Continue statement is used outside loop" }
        return loops.last()
    }
}



private class LoopContext(
    val breakTarget: BasicBlock,
    val continueTarget: BasicBlock
)