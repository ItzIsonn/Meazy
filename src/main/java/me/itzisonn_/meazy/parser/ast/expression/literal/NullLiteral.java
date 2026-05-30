package me.itzisonn_.meazy.parser.ast.expression.literal;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ConstantDescs;

@NullMarked
public class NullLiteral implements Expression {
    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, ProgramUnit parent) {
        instructionsSet.loadNull();
    }

    @Override
    public DataType getType(Environment environment, ProgramUnit parent) {
        return DataType.ofNullable(ConstantDescs.CD_Object);
    }
}
