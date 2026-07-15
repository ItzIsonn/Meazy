package me.itzisonn_.meazy.parser.ast.statement

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment
import me.itzisonn_.meazy.runtime.environment.getParentOrSelf

class ContinueStatement : LocalStatement {
    override val children = setOf<ProgramUnit>()

    context(parents: ParentMap)
    override fun emit(instructions: InstructionsSet, environment: Environment) {
        val loopEnvironment = environment.getParentOrSelf<LoopEnvironment>()
            ?: error("Parent environment for CONTINUE statement must be LoopEnvironment TODO")

        instructions.gotoLabel(loopEnvironment.getStartLabel())
    }

    override fun alwaysReturns() = false
}
