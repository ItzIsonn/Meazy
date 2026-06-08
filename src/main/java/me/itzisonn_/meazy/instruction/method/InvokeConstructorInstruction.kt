package me.itzisonn_.meazy.instruction.method

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import me.itzisonn_.meazy.instruction.InstructionsSet
import java.lang.constant.ClassDesc
import java.lang.constant.MethodTypeDesc
import java.util.function.Consumer

class InvokeConstructorInstruction(
    private val owner: ClassDesc,
    private val constructorTypeDesc: MethodTypeDesc,
    private val argsInstructions: Consumer<InstructionsSet>,
    private val isSuper: Boolean
) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")

        if (!isSuper) {
            codeBuilder.new_(owner)
            codeBuilder.dup()
        }

        val instructionsSet = InstructionsSet(bytecodeBuilders)
        argsInstructions.accept(instructionsSet)

        for (instruction in instructionsSet.instructions) {
            instruction.emit(bytecodeBuilders)
        }

        codeBuilder.invokespecial(owner, "<init>", constructorTypeDesc)
    }
}
