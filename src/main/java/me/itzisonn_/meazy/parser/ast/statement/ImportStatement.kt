package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.runtime.environment.Environment

class ImportStatement(val name: String) : LocalStatement {
    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {}

    override fun alwaysReturns() = false
}
