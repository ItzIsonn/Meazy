package me.itzisonn_.meazy.parser.ast.expression;

import lombok.Getter;

@Getter
public class LogicalExpression implements Expression, ParenthesisExpression {
    private final Expression left;
    private final Expression right;
    private final String operator;

    public LogicalExpression(Expression left, Expression right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public String toCodeString() {
        return left.toCodeString() + " " + operator + " " + right.toCodeString();
    }
}
