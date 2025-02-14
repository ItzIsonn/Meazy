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
        if (expression instanceof ParenthesisExpression) {
            return "-(" + expression.toCodeString() + ")";
        }
        return "-" + expression.toCodeString();
    }
}
