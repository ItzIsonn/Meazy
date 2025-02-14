package me.itzisonn_.meazy.parser.ast.expression;

import lombok.Getter;

@Getter
public class NullCheckExpression implements Expression, ParenthesisExpression {
    private final Expression checkExpression;
    private final Expression nullExpression;

    public NullCheckExpression(Expression checkExpression, Expression nullExpression) {
        this.checkExpression = checkExpression;
        this.nullExpression = nullExpression;
    }

    @Override
    public String toCodeString() {
        return checkExpression.toCodeString() + " ?: " + nullExpression.toCodeString();
    }
}
