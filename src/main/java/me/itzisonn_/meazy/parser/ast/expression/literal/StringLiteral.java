package me.itzisonn_.meazy.parser.ast.expression.literal;

import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ConstantDescs;

@Getter
@NullMarked
public class StringLiteral implements Expression {
    private final String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, ProgramUnit parent) {
        instructionsSet.loadConstant(value);
    }

    @Override
    public DataType getType(Environment environment, ProgramUnit parent) {
        return DataType.ofNonNull(ConstantDescs.CD_String);
    }
}
