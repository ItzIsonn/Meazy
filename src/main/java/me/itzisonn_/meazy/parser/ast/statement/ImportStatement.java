package me.itzisonn_.meazy.parser.ast.statement;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ImportStatement implements LocalStatement {
    private final String name;

    public ImportStatement(String name) {
        this.name = name;
    }

    @Override
    public void emit(InstructionsSet instructions, Environment environment, ProgramUnit parent) {
    }

    @Override
    public boolean alwaysReturns() {
        return false;
    }

    public String getName() {
        return this.name;
    }
}
