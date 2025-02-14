package me.itzisonn_.meazy.parser.ast.expression;

import lombok.Getter;

@Getter
public class InversionExpression implements Expression {
    private final Expression expression;

    public InversionExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toCodeString() {
        if (expression instanceof ParenthesisExpression) {
            return "!(" + expression.toCodeString() + ")";
        }
        return "!" + expression.toCodeString();
    }
}
