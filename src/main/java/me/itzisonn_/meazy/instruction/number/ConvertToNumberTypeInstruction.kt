package me.itzisonn_.meazy.instruction.number

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import me.itzisonn_.meazy.instruction.NumberType
import org.jspecify.annotations.NullMarked
import java.lang.classfile.CodeBuilder
import java.lang.constant.MethodTypeDesc

@NullMarked
class ConvertToNumberTypeInstruction(private val from: NumberType, private val to: NumberType) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")

        if (!from.isBoxed && !to.isBoxed) emitUnboxed(codeBuilder, from, to)
        else if (from.isBoxed && !to.isBoxed) emitBoxedToUnboxed(codeBuilder, from, to)
        else if (!from.isBoxed) emitUnboxedToBoxed(codeBuilder, from, to)
        else emitBoxed(codeBuilder, from, to)
    }

    private fun emitUnboxed(codeBuilder: CodeBuilder, from: NumberType, to: NumberType) {
        when (from) {
            NumberType.INT -> {
                when (to) {
                    NumberType.INT -> {}
                    NumberType.LONG -> codeBuilder.i2l()
                    NumberType.FLOAT -> codeBuilder.i2f()
                    NumberType.DOUBLE -> codeBuilder.i2d()
                    else -> error("Invalid call")
                }
            }

            NumberType.LONG -> {
                when (to) {
                    NumberType.INT -> codeBuilder.l2i()
                    NumberType.LONG -> {}
                    NumberType.FLOAT -> codeBuilder.l2f()
                    NumberType.DOUBLE -> codeBuilder.l2d()
                    else -> error("Invalid call")
                }
            }

            NumberType.FLOAT -> {
                when (to) {
                    NumberType.INT -> codeBuilder.f2i()
                    NumberType.LONG -> codeBuilder.f2l()
                    NumberType.FLOAT -> {}
                    NumberType.DOUBLE -> codeBuilder.f2d()
                    else -> error("Invalid call")
                }
            }

            NumberType.DOUBLE -> {
                when (to) {
                    NumberType.INT -> codeBuilder.d2i()
                    NumberType.LONG -> codeBuilder.d2l()
                    NumberType.FLOAT -> codeBuilder.d2f()
                    NumberType.DOUBLE -> {}
                    else -> error("Invalid call")
                }
            }

            else -> error("Invalid call")
        }
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
