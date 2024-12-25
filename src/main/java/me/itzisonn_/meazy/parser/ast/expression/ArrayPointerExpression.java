package me.itzisonn_.meazy.parser.ast.expression;

import lombok.Getter;

@Getter
public class ArrayPointerExpression implements Expression {
    private final Expression object;
    private final Expression pos;

    public ArrayPointerExpression(Expression object, Expression pos) {
        this.object = object;
        this.pos = pos;
    }

    @Override
    public String toCodeString() {
        return object.toCodeString() + "[" + pos.toCodeString() + "]";
    }
}