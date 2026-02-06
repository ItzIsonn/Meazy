package me.itzisonn_.meazy.parser.ast.expression;

import lombok.Getter;
import org.jspecify.annotations.NullMarked;

@Getter
@NullMarked
public abstract class Identifier implements Expression {
    protected final String id;

    public Identifier(String id) {
        this.id = id;
    }
}