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
        String expressionString = expression.toCodeString();
        if (expressionString.contains(" ")) {
            return "!(" + expression.toCodeString() + ")";
        }
        return "!" + expression.toCodeString();
    }
}
