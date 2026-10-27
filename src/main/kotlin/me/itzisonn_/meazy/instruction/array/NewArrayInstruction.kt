package me.itzisonn_.meazy.instruction.array

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import java.lang.classfile.TypeKind
import java.lang.constant.ClassDesc

class NewArrayInstruction(private val type: ClassDesc) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")

        TypeKind.from(type).let { kind ->
            if (kind.newarrayCode() == -1) codeBuilder.anewarray(type)
            else codeBuilder.newarray(kind)
        }
    }
}
