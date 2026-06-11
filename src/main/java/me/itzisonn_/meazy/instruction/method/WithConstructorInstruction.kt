package me.itzisonn_.meazy.instruction.method

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import me.itzisonn_.meazy.instruction.InstructionsSet
import java.lang.classfile.CodeBuilder
import java.lang.constant.MethodTypeDesc
import java.lang.reflect.AccessFlag

class WithConstructorInstruction(
    private val methodTypeDesc: MethodTypeDesc,
    private val flags: Int,
    private val bodyInstructions: (InstructionsSet) -> Unit
) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val classBuilder = bytecodeBuilders.classBuilder ?: error("Class builder is null")
        val isStatic = AccessFlag.maskToAccessFlags(flags, AccessFlag.Location.METHOD).contains(AccessFlag.STATIC)

        classBuilder.withMethodBody(
            if (isStatic) "<clinit>" else "<init>",
            methodTypeDesc,
            flags
        ) { codeBuilder: CodeBuilder? ->
            val constructorBytecodeBuilders = bytecodeBuilders.copy(codeBuilder)
            val instructionsSet = InstructionsSet(constructorBytecodeBuilders)

            bodyInstructions(instructionsSet)
            for (instruction in instructionsSet.instructions) {
                instruction.emit(constructorBytecodeBuilders)
            }
        }
    }
}
