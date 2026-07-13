package me.itzisonn_.meazy.parser.ast.expression.identifier

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.runtime.environment.Environment

class FunctionIdentifier(id: String) : Identifier(id) {
    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        error("Can't emit function identifier")
    }

    override fun getType(environment: Environment, parent: ProgramUnit): DataType {
        error("Can't get type of function identifier")
    }
}