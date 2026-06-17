package me.itzisonn_.meazy.parser.ast

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.runtime.environment.Environment

interface ProgramUnit {
    fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit)
}
