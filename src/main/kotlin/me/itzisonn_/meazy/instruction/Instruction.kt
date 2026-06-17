package me.itzisonn_.meazy.instruction

interface Instruction {
    fun emit(bytecodeBuilders: BytecodeBuilders)
}
