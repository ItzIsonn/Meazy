package me.itzisonn_.meazy.parser.ast.expression.call_expression;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.expression.Expression;

import java.util.List;

@Getter
public abstract class CallExpression implements Expression {
    protected final Expression caller;
    protected final List<Expression> args;

    public CallExpression(Expression caller, List<Expression> args) {
        this.caller = caller;
        this.args = args;
    }
}