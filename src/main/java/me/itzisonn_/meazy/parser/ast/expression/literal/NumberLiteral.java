package me.itzisonn_.meazy.parser.ast.expression.literal;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.expression.Expression;

@Getter
public class NumberLiteral implements Expression {
    private final String value;

    public NumberLiteral(String value) {
        this.value = value;
    }

    @Override
    public String toCodeString() {
        return value;
    }
}
