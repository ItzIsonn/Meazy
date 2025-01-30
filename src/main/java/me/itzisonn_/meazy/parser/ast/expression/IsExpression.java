package me.itzisonn_.meazy.parser.ast.expression;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IsExpression implements Expression {
    private final Expression value;
    private final String dataType;

    public IsExpression(Expression value, String dataType) {
        this.value = value;
        this.dataType = dataType;
    }

    @Override
    public String toCodeString() {
        return value.toCodeString() + " is " + dataType;
    }
}