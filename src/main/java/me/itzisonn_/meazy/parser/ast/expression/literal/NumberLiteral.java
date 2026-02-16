package me.itzisonn_.meazy.parser.ast.expression.literal;

import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ConstantDescs;

@Getter
@NullMarked
public class NumberLiteral implements Expression {
    private final String value;

    public NumberLiteral(String value) {
        this.value = value;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {
        try {
            int number = Integer.parseInt(value);
            instructionsSet.loadConstant(number);
        }
        catch (NumberFormatException e) {
            double number = Double.parseDouble(value);
            instructionsSet.loadConstant(number);
        }
    }

    @Override
    public DataType getType(Environment environment, Statement parent) {
        try {
            Integer.parseInt(value);
            return DataType.ofNonNull(ConstantDescs.CD_int);
        }
        catch (NumberFormatException e) {
            return DataType.ofNonNull(ConstantDescs.CD_double);
        }
    }
}
