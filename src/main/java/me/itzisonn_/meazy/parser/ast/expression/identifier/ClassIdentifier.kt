package me.itzisonn_.meazy.parser.ast.expression.identifier

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils.resolveClassDesc

open class ClassIdentifier(id: String) : Identifier(id) {
    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        error("Can't emit class identifier")
    }

    override fun getType(environment: Environment, parent: ProgramUnit): DataType {
        return DataType.ofNonNull(resolveClassDesc(environment, id, false))
    }
}