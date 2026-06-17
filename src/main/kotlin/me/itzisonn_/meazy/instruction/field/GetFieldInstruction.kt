package me.itzisonn_.meazy.instruction.field

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import java.lang.constant.ClassDesc

class GetFieldInstruction(
    private val owner: ClassDesc,
    private val id: String,
    private val type: ClassDesc,
    private val isStatic: Boolean
) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")

        if (isStatic) codeBuilder.getstatic(owner, id, type)
        else codeBuilder.getfield(owner, id, type)
    }
}
