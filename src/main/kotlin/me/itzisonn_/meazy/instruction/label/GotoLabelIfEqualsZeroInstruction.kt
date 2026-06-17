package me.itzisonn_.meazy.instruction.label

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import kotlin.uuid.Uuid

class GotoLabelIfEqualsZeroInstruction(private val uuid: Uuid) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")
        codeBuilder.ifeq(bytecodeBuilders.getLabel(uuid))
    }
}
