package me.itzisonn_.meazy.parser.ast.expression;

import lombok.Getter;

@Getter
public abstract class Identifier implements Expression {
    protected final String id;

    public Identifier(String id) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");
        this.id = id;
    }

    @Override
    public String toCodeString() {
        return id;
    }
}