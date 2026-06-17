package me.itzisonn_.meazy.instruction.field

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import java.lang.constant.ClassDesc

class WithFieldInstruction(
    private val id: String,
    private val type: ClassDesc,
    private val flags: Int
) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val classBuilder = bytecodeBuilders.classBuilder ?: error("Class builder is null")
        classBuilder.withField(id, type, flags)
    }
}
