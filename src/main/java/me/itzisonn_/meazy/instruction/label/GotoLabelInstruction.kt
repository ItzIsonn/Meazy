package me.itzisonn_.meazy.instruction.label

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import kotlin.uuid.Uuid

class GotoLabelInstruction(private val uuid: Uuid) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: throw RuntimeException("Code builder is null")
        codeBuilder.goto_(bytecodeBuilders.getLabel(uuid))
    }
}
