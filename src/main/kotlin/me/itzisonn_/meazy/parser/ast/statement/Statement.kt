package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.runtime.environment.Environment

/**
 * Represents program unit with multiple lines possible
 */
interface Statement : ProgramUnit

interface LocalStatement : Statement {
    fun alwaysReturns(): Boolean
}

interface DeclarationStatement : Statement {
    context(parents: ParentMap)
    fun declare(environment: Environment)

    context(parents: ParentMap)
    fun resolve(environment: Environment)
}