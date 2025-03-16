package me.itzisonn_.meazy.parser.operator;

import lombok.Getter;

@Getter
public class Operator {
    private final String operator;
    private final boolean isInfix;

    public Operator(String operator, boolean isInfix) {
        this.operator = operator;
        this.isInfix = isInfix;
    }
}
