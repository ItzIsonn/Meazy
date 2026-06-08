package me.itzisonn_.meazy.instruction.number

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import me.itzisonn_.meazy.instruction.NumberType
import org.jspecify.annotations.NullMarked

@NullMarked
class NegateNumberInstruction(private val type: NumberType) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")

        when (type) {
            NumberType.INT -> codeBuilder.ineg()
            NumberType.LONG -> codeBuilder.lneg()
            NumberType.FLOAT -> codeBuilder.fneg()
            NumberType.DOUBLE -> codeBuilder.dneg()
            else -> error("Can't negate boxed number $type")
        }
    }
}
