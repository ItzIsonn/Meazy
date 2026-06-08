package me.itzisonn_.meazy.instruction.misc

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import org.jspecify.annotations.NullMarked
import java.lang.constant.ClassDesc

@NullMarked
class InstanceOfInstruction(private val target: ClassDesc) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")
        codeBuilder.instanceOf(target)
    }
}
