package me.itzisonn_.meazy.instruction.method

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import me.itzisonn_.meazy.instruction.InstructionsSet
import java.lang.constant.ClassDesc
import java.lang.constant.MethodTypeDesc
import java.util.function.Consumer

class InvokeMethodInstruction(
    private val owner: ClassDesc,
    private val id: String,
    private val methodTypeDesc: MethodTypeDesc,
    private val argsInstructions: Consumer<InstructionsSet>,
    private val invokeType: InvokeType
) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")

        val instructionsSet = InstructionsSet(bytecodeBuilders)
        argsInstructions.accept(instructionsSet)

        for (instruction in instructionsSet.instructions) {
            instruction.emit(bytecodeBuilders)
        }

        when (invokeType) {
            InvokeType.STATIC -> codeBuilder.invokestatic(owner, id, methodTypeDesc, false)
            InvokeType.STATIC_INTERFACE -> codeBuilder.invokestatic(owner, id, methodTypeDesc, true)
            InvokeType.INTERFACE -> codeBuilder.invokeinterface(owner, id, methodTypeDesc)
            InvokeType.VIRTUAL -> codeBuilder.invokevirtual(owner, id, methodTypeDesc)
            InvokeType.SPECIAL -> codeBuilder.invokespecial(owner, id, methodTypeDesc)
        }
    }

    enum class InvokeType {
        STATIC,
        STATIC_INTERFACE,
        INTERFACE,
        VIRTUAL,
        SPECIAL
    }
}
