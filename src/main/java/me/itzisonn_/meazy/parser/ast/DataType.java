package me.itzisonn_.meazy.parser.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public abstract class DataType {
    private final String name;

    public DataType(String name) {
        this.name = name;
    }

    public abstract boolean isMatches(Object value);
}