package me.itzisonn_.meazy.parser.ast.expression.literal;

import lombok.Getter;
import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.parser.ast.expression.Expression;

@Getter
public class NumberLiteral implements Expression {
    private final double value;
    private final boolean isInt;

    public NumberLiteral(double value, boolean isInt) {
        this.value = value;
        this.isInt = isInt;
    }

    @Override
    public String toCodeString() {
        return Utils.NUMBER_FORMAT.format(value);
    }
}
