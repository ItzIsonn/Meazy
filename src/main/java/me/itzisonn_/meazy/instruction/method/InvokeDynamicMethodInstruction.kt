package me.itzisonn_.meazy.instruction.method

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import java.lang.constant.ConstantDesc
import java.lang.constant.DirectMethodHandleDesc
import java.lang.constant.DynamicCallSiteDesc
import java.lang.constant.MethodTypeDesc

class InvokeDynamicMethodInstruction(
    private val bootstrapMethod: DirectMethodHandleDesc,
    private val id: String,
    private val methodTypeDesc: MethodTypeDesc,
    private val args: MutableList<ConstantDesc>
) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")

        codeBuilder.invokedynamic(
            DynamicCallSiteDesc.of(
                bootstrapMethod,
                id,
                methodTypeDesc,
                *args.toTypedArray<ConstantDesc>()
            )
        )
    }
}
