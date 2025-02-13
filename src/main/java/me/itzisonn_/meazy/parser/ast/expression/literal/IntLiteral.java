package me.itzisonn_.meazy.parser.ast.expression.literal;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.expression.Expression;

@Getter
public class IntLiteral implements Expression {
    private final int value;

    public IntLiteral(int value) {
        this.value = value;
    }

    @Override
    public String toCodeString() {
        return String.valueOf(value);
    }
}
