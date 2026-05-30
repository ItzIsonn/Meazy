package me.itzisonn_.meazy.parser.ast.expression.identifier;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FunctionIdentifier extends Identifier {
    public FunctionIdentifier(String id) {
        super(id);
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, ProgramUnit parent) {
        throw new UnsupportedOperationException("Can't emit function identifier");
    }

    @Override
    public DataType getType(Environment environment, ProgramUnit parent) {
        throw new UnsupportedOperationException("Can't get type of function identifier");
    }
}