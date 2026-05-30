package me.itzisonn_.meazy.parser.ast.expression.identifier;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ClassIdentifier extends Identifier {
    public ClassIdentifier(String id) {
        super(id);
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, ProgramUnit parent) {
        throw new UnsupportedOperationException("Can't emit class identifier");
    }

    @Override
    public DataType getType(Environment environment, ProgramUnit parent) {
        return DataType.ofNonNull(EnvironmentUtils.resolveClassDesc(environment, id, false));
    }
}