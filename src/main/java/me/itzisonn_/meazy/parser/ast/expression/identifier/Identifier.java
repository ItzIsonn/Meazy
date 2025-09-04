package me.itzisonn_.meazy.parser.ast.expression.identifier;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.expression.Expression;

@Getter
public abstract class Identifier implements Expression {
    protected final String id;

    public Identifier(String id) {
        this.id = id;
    }
}