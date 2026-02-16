package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.version.Version;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@Getter
@NullMarked
public class RequireStatement implements Statement {
    private final String id;
    @Nullable
    private final Version version;

    public RequireStatement(String id, @Nullable Version version) {
        this.id = id;
        this.version = version;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {}
}
