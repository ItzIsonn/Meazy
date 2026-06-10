package me.itzisonn_.meazy.instruction.misc

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import me.itzisonn_.meazy.instruction.InstructionsSet
import java.lang.classfile.attribute.InnerClassesAttribute
import java.lang.constant.ClassDesc

class WithClassInstruction(
    private val classDesc: ClassDesc,
    private val superClass: ClassDesc?,
    private val interfaceClasses: Set<ClassDesc>,
    private val flags: Int,
    private val attributes: List<InnerClassesAttribute>,
    private val classInstructions: (InstructionsSet) -> Unit
) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        bytecodeBuilders.withClass(classDesc) { classBuilder ->
            classBuilder.withFlags(flags)
            for (attribute in attributes) classBuilder.with(attribute)

            if (superClass != null) classBuilder.withSuperclass(superClass)
            classBuilder.withInterfaceSymbols(interfaceClasses.toList())

            val classBytecodeBuilders = bytecodeBuilders.copy(classBuilder)
            val instructionsSet = InstructionsSet(classBytecodeBuilders)

            classInstructions(instructionsSet)
            for (instruction in instructionsSet.instructions) {
                instruction.emit(classBytecodeBuilders)
            }
        }
    }
}
