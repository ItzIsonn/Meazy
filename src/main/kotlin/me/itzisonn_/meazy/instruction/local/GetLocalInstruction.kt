package me.itzisonn_.meazy.instruction.local

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import java.lang.classfile.TypeKind
import java.lang.constant.ClassDesc

class GetLocalInstruction(private val type: ClassDesc, private val slot: Int) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")
        codeBuilder.loadLocal(TypeKind.from(type), slot)
    }
}
