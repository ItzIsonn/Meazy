package me.itzisonn_.meazy.instruction.misc

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import me.itzisonn_.meazy.instruction.InstructionsSet
import org.jspecify.annotations.NullMarked
import java.lang.classfile.ClassBuilder
import java.lang.classfile.attribute.InnerClassesAttribute
import java.lang.constant.ClassDesc
import java.util.function.Consumer

@NullMarked
class WithClassInstruction(
    private val classDesc: ClassDesc,
    private val superClass: ClassDesc?,
    private val interfaceClasses: MutableSet<ClassDesc>,
    private val flags: Int,
    private val attributes: MutableList<InnerClassesAttribute>,
    private val classInstructions: Consumer<InstructionsSet>
) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        bytecodeBuilders.withClass(
            classDesc
        ) { classBuilder: ClassBuilder? ->
            classBuilder!!.withFlags(flags)
            for (attribute in attributes) classBuilder.with(attribute)

            if (superClass != null) classBuilder.withSuperclass(superClass)
            classBuilder.withInterfaceSymbols(*interfaceClasses.toTypedArray<ClassDesc>())

            val classBytecodeBuilders = bytecodeBuilders.copy(classBuilder)
            val instructionsSet = InstructionsSet(classBytecodeBuilders)

            classInstructions.accept(instructionsSet)
            for (instruction in instructionsSet.instructions) {
                instruction.emit(classBytecodeBuilders)
            }
        }
    }
}
