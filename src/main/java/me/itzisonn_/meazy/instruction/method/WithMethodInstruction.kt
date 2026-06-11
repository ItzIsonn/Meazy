package me.itzisonn_.meazy.instruction.method

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import me.itzisonn_.meazy.instruction.InstructionsSet
import java.lang.constant.MethodTypeDesc
import java.lang.reflect.AccessFlag

class WithMethodInstruction(
    private val id: String,
    private val methodTypeDesc: MethodTypeDesc,
    private val flags: Int,
    private val bodyInstructions: (InstructionsSet) -> Unit
) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val classBuilder = bytecodeBuilders.classBuilder ?: error("Class builder is null")

        if ((AccessFlag.ABSTRACT.mask() and flags) != 0) {
            classBuilder.withMethod(id, methodTypeDesc, flags) {}
            return
        }

        classBuilder.withMethodBody(
            id,
            methodTypeDesc,
            flags
        ) { codeBuilder ->
            val methodBytecodeBuilders = bytecodeBuilders.copy(codeBuilder)
            val instructionsSet = InstructionsSet(methodBytecodeBuilders)

            bodyInstructions(instructionsSet)
            for (instruction in instructionsSet.instructions) {
                instruction.emit(methodBytecodeBuilders)
            }
        }
    }
}
