package me.itzisonn_.meazy.parser.cfg.helper

import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.statement.BaseCallStatement
import me.itzisonn_.meazy.parser.ast.statement.ConstructorDeclarationStatement
import me.itzisonn_.meazy.parser.ast.statement.IfStatement
import me.itzisonn_.meazy.parser.ast.statement.IfStatementCase
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement
import me.itzisonn_.meazy.parser.ast.statement.ReturnStatement

object ConstructorNormalizer {
    fun normalize(constructor: ConstructorDeclarationStatement, hasAccessibleNoArgSuperConstructor: Boolean) {
        val result = rewriteStatements(constructor.body, false)
        val rewrittenBody = result.statements.toMutableList()

        when (result.initialized) {
            false -> {
                if (!hasAccessibleNoArgSuperConstructor) {
                    error("Missing base() call, and there's no accessible base class constructor without args")
                }

                rewrittenBody.add(BaseCallStatement(listOf()))
                rewrittenBody.add(ReturnStatement(null))
            }

            true -> rewrittenBody.add(ReturnStatement(null))
            null -> {}
        }

        constructor.body.clear()
        constructor.body.addAll(rewrittenBody)
    }

    private fun rewriteStatements(statements: List<LocalStatement>, initialized: Boolean): RewriteResult {
        val result = mutableListOf<LocalStatement>()
        var currentState: Boolean? = initialized

        for (statement in statements) {
            if (currentState == null) {
                result.add(statement)
                continue
            }

            val rewritten = rewriteStatement(statement, currentState)
            result.addAll(rewritten.statements)
            currentState = rewritten.initialized
        }

        return RewriteResult(result, currentState)
    }

    private fun rewriteStatement(statement: LocalStatement, initialized: Boolean): RewriteResult {
        return when (statement) {
            is BaseCallStatement -> {
                if (initialized) error("Base class constructor called more than once on this path")
                RewriteResult(listOf(statement), true)
            }

//            is ThisCallStmt -> {
//                if (initialized) error("Constructor delegation called after initialization")
//                RewriteResult(listOf(statement), true)
//            }

            is ReturnStatement -> {
                val result = mutableListOf<LocalStatement>()
                if (!initialized) result.add(BaseCallStatement(listOf()))

                result.add(statement)
                RewriteResult(result, null)
            }

            is IfStatement -> rewriteIfStatement(statement, initialized)
            else -> RewriteResult(listOf(statement), initialized)
        }
    }

    private fun rewriteIfStatement(statement: IfStatement, initialized: Boolean): RewriteResult {
        val rewrittenCases = statement.cases.map { case ->
            val bodyResult = rewriteStatements(case.body, initialized)
            RewrittenCase(case.condition, bodyResult.statements, bodyResult.initialized)
        }.toMutableList()

        val hasElse = rewrittenCases.any { it.condition == null }
        val implicitElseState = if (hasElse) null else initialized

        val normalStates = buildList {
            rewrittenCases.forEach { branch ->
                branch.normalState?.let(::add)
            }

            implicitElseState?.let(::add)
        }

        if (normalStates.isEmpty()) {
            val newIf = IfStatement(rewrittenCases.map { IfStatementCase(it.condition, it.body) })
            return RewriteResult(listOf(newIf), null)
        }

        val allInitialized = normalStates.all { it }
        val allUninitialized = normalStates.none { it }

        if (allInitialized || allUninitialized) {
            val newIf = IfStatement(rewrittenCases.map { IfStatementCase(it.condition, it.body) })
            return RewriteResult(listOf(newIf), allInitialized)
        }

        rewrittenCases.forEach { branch ->
            if (branch.normalState != false) return@forEach

            branch.body.toMutableList().also { body ->
                body.add(BaseCallStatement(listOf()))
                val index = rewrittenCases.indexOf(branch)
                rewrittenCases[index] = branch.copy(body = body, normalState = true)
            }
        }

        if (!hasElse && implicitElseState == false) {
            rewrittenCases.add(RewrittenCase(null, listOf(BaseCallStatement(listOf())), true))
        }

        val newIf = IfStatement(rewrittenCases.map { IfStatementCase(it.condition, it.body) })
        return RewriteResult(listOf(newIf), true)
    }
}



private data class RewriteResult(
    val statements: List<LocalStatement>,
    val initialized: Boolean?
)

private data class RewrittenCase(
    val condition: Expression?,
    val body: List<LocalStatement>,
    var normalState: Boolean?
)