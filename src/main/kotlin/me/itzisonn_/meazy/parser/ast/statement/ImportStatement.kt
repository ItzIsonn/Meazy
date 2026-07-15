package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.runtime.environment.Environment

class ImportStatement(val name: String) : LocalStatement {
    override val children = setOf<ProgramUnit>()

    context(parents: ParentMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {}

    override fun alwaysReturns() = false
}
