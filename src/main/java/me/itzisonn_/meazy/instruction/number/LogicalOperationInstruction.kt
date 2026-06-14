package me.itzisonn_.meazy.instruction.number

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction

class LogicalOperationInstruction(private val operation: LogicalOperation) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")

        when (operation) {
            LogicalOperation.OR -> codeBuilder.ior()
            LogicalOperation.AND -> codeBuilder.iand()
        }
    }

    enum class LogicalOperation {
        OR,
        AND
    }
}
