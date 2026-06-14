package me.itzisonn_.meazy.instruction.local

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.Instruction
import java.lang.constant.ClassDesc
import kotlin.uuid.Uuid

class SetLocalNameInstruction(
    private val slot: Int,
    private val id: String,
    private val type: ClassDesc,
    private val startUuid: Uuid,
    private val endUuid: Uuid
) : Instruction {
    override fun emit(bytecodeBuilders: BytecodeBuilders) {
        val codeBuilder = bytecodeBuilders.codeBuilder ?: error("Code builder is null")

        codeBuilder.localVariable(
            slot,
            id,
            type,
            bytecodeBuilders.getLabel(startUuid),
            bytecodeBuilders.getLabel(endUuid)
        )
    }
}
