package me.itzisonn_.meazy.instruction.misc

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import java.lang.classfile.TypeKind
import java.lang.constant.ClassDesc

class ReturnInstruction(private val type: ClassDesc?) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")

        if (type != null) codeBuilder.return_(TypeKind.from(type))
        else codeBuilder.return_()
    }
}
