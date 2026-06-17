package me.itzisonn_.meazy.instruction.number

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

class ConvertToBooleanTypeInstruction(private val isFromBoxed: Boolean, private val isToBoxed: Boolean) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")
        if (isFromBoxed == isToBoxed) return

        if (isFromBoxed) {
            codeBuilder.invokevirtual(
                ConstantDescs.CD_Boolean,
                "booleanValue",
                MethodTypeDesc.of(ConstantDescs.CD_boolean)
            )
        }
        else {
            codeBuilder.invokestatic(
                ConstantDescs.CD_Boolean,
                "valueOf",
                MethodTypeDesc.of(ConstantDescs.CD_Boolean, ConstantDescs.CD_boolean)
            )
        }
    }
}
