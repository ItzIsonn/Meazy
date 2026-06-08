package me.itzisonn_.meazy.instruction.stack

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import org.jspecify.annotations.NullMarked
import java.lang.constant.ConstantDesc

@NullMarked
class LoadConstantInstruction(private val constant: ConstantDesc?) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")

        if (constant != null) codeBuilder.loadConstant(constant)
        else codeBuilder.aconst_null()
    }
}
