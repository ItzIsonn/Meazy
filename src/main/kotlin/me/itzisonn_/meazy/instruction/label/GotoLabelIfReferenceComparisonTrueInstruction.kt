package me.itzisonn_.meazy.instruction.label

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import kotlin.uuid.Uuid

class GotoLabelIfReferenceComparisonTrueInstruction(
    private val operation: ComparisonOperation,
    private val uuid: Uuid
) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")
        val label = bytecodeBuilders.getLabel(uuid)

        when (operation) {
            ComparisonOperation.EQUALS -> codeBuilder.if_acmpeq(label)
            ComparisonOperation.NOT_EQUALS -> codeBuilder.if_acmpne(label)
            else -> error("Unsupported operation: $operation")
        }
    }
}
