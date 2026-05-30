package me.itzisonn_.meazy.parser.ast;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface ProgramUnit {
    void emit(InstructionsSet instructionsSet, Environment environment, ProgramUnit parent);
}
