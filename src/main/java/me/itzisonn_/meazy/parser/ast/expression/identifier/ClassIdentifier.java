package me.itzisonn_.meazy.parser.ast.expression.identifier;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Identifier;
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
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {
        throw new UnsupportedOperationException("Can't emit class identifier");
    }

    @Override
    public DataType getType(Environment environment, Statement parent) {
        return DataType.ofNonNull(EnvironmentUtils.resolveClassDesc(environment, id));
    }
}