package me.itzisonn_.meazy.parser.ast.expression

import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.runtime.environment.Environment

/**
 * Represents unit of the program with only one (not-full) line possible
 */
interface Expression : ProgramUnit {
    fun getType(environment: Environment, parent: ProgramUnit): DataType
}
