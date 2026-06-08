package me.itzisonn_.meazy.instruction.label

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import me.itzisonn_.meazy.instruction.NumberType
import org.jspecify.annotations.NullMarked
import kotlin.uuid.Uuid

@NullMarked
class GotoLabelIfComparisonTrueInstruction(
    private val type: NumberType,
    private val operation: ComparisonOperation,
    private val uuid: Uuid
) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")
        val label = bytecodeBuilders.getLabel(uuid)

        if (type == NumberType.INT) {
            when (operation) {
                ComparisonOperation.EQUALS -> codeBuilder.if_icmpeq(label)
                ComparisonOperation.NOT_EQUALS -> codeBuilder.if_icmpne(label)
                ComparisonOperation.GREATER -> codeBuilder.if_icmpgt(label)
                ComparisonOperation.GREATER_OR_EQUALS -> codeBuilder.if_icmpge(label)
                ComparisonOperation.LESS -> codeBuilder.if_icmplt(label)
                ComparisonOperation.LESS_OR_EQUALS -> codeBuilder.if_icmple(label)
            }

            return
        }

        when (type) {
            NumberType.LONG -> codeBuilder.lcmp()
            NumberType.FLOAT -> codeBuilder.fcmpl()
            NumberType.DOUBLE -> codeBuilder.dcmpl()
            else -> error("Can't compare boxed number $type")
        }

        when (operation) {
            ComparisonOperation.EQUALS -> codeBuilder.ifeq(label)
            ComparisonOperation.NOT_EQUALS -> codeBuilder.ifne(label)
            ComparisonOperation.GREATER -> codeBuilder.ifgt(label)
            ComparisonOperation.GREATER_OR_EQUALS -> codeBuilder.ifge(label)
            ComparisonOperation.LESS -> codeBuilder.iflt(label)
            ComparisonOperation.LESS_OR_EQUALS -> codeBuilder.ifle(label)
        }
    }

    enum class ComparisonOperation {
        EQUALS,
        NOT_EQUALS,
        GREATER,
        GREATER_OR_EQUALS,
        LESS,
        LESS_OR_EQUALS
    }
}
