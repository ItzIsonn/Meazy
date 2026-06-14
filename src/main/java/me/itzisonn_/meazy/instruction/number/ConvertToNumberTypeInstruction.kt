package me.itzisonn_.meazy.instruction.number

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import me.itzisonn_.meazy.instruction.NumberType
import java.lang.classfile.CodeBuilder
import java.lang.classfile.TypeKind
import java.lang.constant.MethodTypeDesc

class ConvertToNumberTypeInstruction(private val from: NumberType, private val to: NumberType) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")

        if (!from.isBoxed && !to.isBoxed) emitUnboxed(codeBuilder, from, to)
        else if (from.isBoxed && !to.isBoxed) emitBoxedToUnboxed(codeBuilder, from, to)
        else if (!from.isBoxed) emitUnboxedToBoxed(codeBuilder, from, to)
        else emitBoxed(codeBuilder, from, to)
    }

    private fun emitUnboxed(codeBuilder: CodeBuilder, from: NumberType, to: NumberType) {
        codeBuilder.conversion(
            TypeKind.from(from.classDesc),
            TypeKind.from(to.classDesc)
        )
    }

    private fun emitBoxedToUnboxed(codeBuilder: CodeBuilder, from: NumberType, to: NumberType) {
        val methodName = when (to) {
            NumberType.INT -> "intValue"
            NumberType.LONG -> "longValue"
            NumberType.FLOAT -> "floatValue"
            NumberType.DOUBLE -> "doubleValue"
            else -> error("Invalid call")
        }

        codeBuilder.invokevirtual(from.classDesc, methodName, MethodTypeDesc.of(to.classDesc))
    }

    private fun emitUnboxedToBoxed(codeBuilder: CodeBuilder, from: NumberType, to: NumberType) {
        emitUnboxed(codeBuilder, from, to.unbox())
        codeBuilder.invokestatic(
            to.classDesc,
            "valueOf",
            MethodTypeDesc.of(to.classDesc, to.unbox().classDesc)
        )
    }

    private fun emitBoxed(codeBuilder: CodeBuilder, from: NumberType, to: NumberType) {
        emitBoxedToUnboxed(codeBuilder, from, from.unbox())
        emitUnboxedToBoxed(codeBuilder, from.unbox(), to)
    }
}
