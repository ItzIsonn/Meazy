package me.itzisonn_.meazy.instruction;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface Instruction {
    void emit(BytecodeBuilders bytecodeBuilders);
}
