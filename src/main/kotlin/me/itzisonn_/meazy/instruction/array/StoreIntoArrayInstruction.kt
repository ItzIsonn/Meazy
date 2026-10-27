package me.itzisonn_.meazy.instruction.array

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import java.lang.classfile.TypeKind
import java.lang.constant.ClassDesc

class StoreIntoArrayInstruction(private val type: ClassDesc) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")
        codeBuilder.arrayStore(TypeKind.from(type))
    }
}
