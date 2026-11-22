package me.itzisonn_.meazy.instruction.control_flow

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import me.itzisonn_.meazy.instruction.InstructionsSet

class IfThenInstruction(private val thenInstructions: (InstructionsSet) -> Unit) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")
        codeBuilder.ifThen { thenBuilder ->
            val thenBytecodeBuilders = bytecodeBuilders.copy(thenBuilder)
            val instructionsSet = InstructionsSet(thenBytecodeBuilders)

            thenInstructions(instructionsSet)
            for (instruction in instructionsSet.instructions) {
                instruction.emit(thenBytecodeBuilders)
            }
        }
    }
}
