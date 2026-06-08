package me.itzisonn_.meazy.instruction.method

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import me.itzisonn_.meazy.instruction.InstructionsSet
import java.lang.classfile.CodeBuilder
import java.lang.classfile.MethodBuilder
import java.lang.constant.MethodTypeDesc
import java.lang.reflect.AccessFlag
import java.util.function.Consumer

class WithMethodInstruction(
    private val id: String,
    private val methodTypeDesc: MethodTypeDesc,
    private val flags: Int,
    private val bodyInstructions: Consumer<InstructionsSet>
) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val classBuilder = bytecodeBuilders.classBuilder ?: error("Class builder is null")

        if ((AccessFlag.ABSTRACT.mask() and flags) != 0) {
            classBuilder.withMethod(id, methodTypeDesc, flags, Consumer { `_`: MethodBuilder? -> })
            return
        }

        classBuilder.withMethodBody(
            id,
            methodTypeDesc,
            flags
        ) { codeBuilder: CodeBuilder? ->
            val methodBytecodeBuilders = bytecodeBuilders.copy(codeBuilder)
            val instructionsSet = InstructionsSet(methodBytecodeBuilders)

            bodyInstructions.accept(instructionsSet)
            for (instruction in instructionsSet.instructions) {
                instruction.emit(methodBytecodeBuilders)
            }
        }
    }
}
