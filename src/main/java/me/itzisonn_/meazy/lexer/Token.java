package me.itzisonn_.meazy.lexer;

import lombok.Getter;

/**
 * Represents code unit
 */
@Getter
public class Token {
    private final int line;
    private final int start;
    private final int end;
    private final TokenType type;
    private final String value;

    /**
     * @param line Line on which this token is located
     * @param start Start index of this token
     * @param end End index of this token
     * @param type TokenType
     * @param value String that matches this token's type
     *
     * @throws IllegalArgumentException If line is negative
     * @throws NullPointerException If either type or value is null
     */
    public Token(int line, int start, int end, TokenType type, String value) {
        if (line < 0) throw new IllegalArgumentException("Line can't be negative");
        if (start < 0) throw new IllegalArgumentException("Start can't be negative");
        if (end < 0) throw new IllegalArgumentException("End can't be negative");
        if (type == null) throw new NullPointerException("Type can't be null");
        if (value == null) throw new NullPointerException("Value can't be null");

        this.start = start;
        this.end = end;
        this.line = line;
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Token(" + line + "," + type + "," + value.replaceAll("\n", "\\\\n") + ")";
    }
}