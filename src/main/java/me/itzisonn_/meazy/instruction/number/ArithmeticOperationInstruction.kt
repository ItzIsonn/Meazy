package me.itzisonn_.meazy.instruction.number

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import me.itzisonn_.meazy.instruction.NumberType
import org.jspecify.annotations.NullMarked

@NullMarked
class ArithmeticOperationInstruction(
    private val type: NumberType,
    private val operation: ArithmeticOperation
) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")

        when (type) {
            NumberType.INT -> {
                when (operation) {
                    ArithmeticOperation.ADDITION -> codeBuilder.iadd()
                    ArithmeticOperation.SUBTRACTION -> codeBuilder.isub()
                    ArithmeticOperation.MULTIPLICATION -> codeBuilder.imul()
                    ArithmeticOperation.DIVISION -> codeBuilder.idiv()
                    ArithmeticOperation.REMAINDER -> codeBuilder.irem()
                }
            }

            NumberType.LONG -> {
                when (operation) {
                    ArithmeticOperation.ADDITION -> codeBuilder.ladd()
                    ArithmeticOperation.SUBTRACTION -> codeBuilder.lsub()
                    ArithmeticOperation.MULTIPLICATION -> codeBuilder.lmul()
                    ArithmeticOperation.DIVISION -> codeBuilder.ldiv()
                    ArithmeticOperation.REMAINDER -> codeBuilder.lrem()
                }
            }

            NumberType.FLOAT -> {
                when (operation) {
                    ArithmeticOperation.ADDITION -> codeBuilder.fadd()
                    ArithmeticOperation.SUBTRACTION -> codeBuilder.fsub()
                    ArithmeticOperation.MULTIPLICATION -> codeBuilder.fmul()
                    ArithmeticOperation.DIVISION -> codeBuilder.fdiv()
                    ArithmeticOperation.REMAINDER -> codeBuilder.frem()
                }
            }

            NumberType.DOUBLE -> {
                when (operation) {
                    ArithmeticOperation.ADDITION -> codeBuilder.dadd()
                    ArithmeticOperation.SUBTRACTION -> codeBuilder.dsub()
                    ArithmeticOperation.MULTIPLICATION -> codeBuilder.dmul()
                    ArithmeticOperation.DIVISION -> codeBuilder.ddiv()
                    ArithmeticOperation.REMAINDER -> codeBuilder.drem()
                }
            }

            else -> error("Can't apply arithmetic operation to boxed number $type")
        }
    }

    enum class ArithmeticOperation {
        ADDITION,
        SUBTRACTION,
        MULTIPLICATION,
        DIVISION,
        REMAINDER
    }
}
