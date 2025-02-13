package me.itzisonn_.meazy.parser.ast.expression;

import lombok.Getter;

@Getter
public class NegationExpression implements Expression {
    private final Expression expression;

    public NegationExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toCodeString() {
        String expressionString = expression.toCodeString();
        if (expressionString.contains(" ")) {
            return "-(" + expression.toCodeString() + ")";
        }
        return "-" + expression.toCodeString();
    }
}
