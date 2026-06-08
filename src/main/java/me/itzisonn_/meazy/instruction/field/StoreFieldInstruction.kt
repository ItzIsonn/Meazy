package me.itzisonn_.meazy.instruction.field

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import org.jspecify.annotations.NullMarked
import java.lang.constant.ClassDesc

@NullMarked
class StoreFieldInstruction(
    private val owner: ClassDesc,
    private val id: String,
    private val type: ClassDesc,
    private val isStatic: Boolean
) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")

        if (isStatic) codeBuilder.putstatic(owner, id, type)
        else codeBuilder.putfield(owner, id, type)
    }
}
