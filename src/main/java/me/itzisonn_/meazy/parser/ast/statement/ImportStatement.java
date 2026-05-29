package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

@Getter
@NullMarked
public class ImportStatement implements Statement {
    private final String name;

    public ImportStatement(String name) {
        this.name = name;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {}
}
