package me.itzisonn_.meazy.lexer;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Token that is used to split string
 */
@Getter
@EqualsAndHashCode
public class Token {
    /**
     * Line on which this Token is located
     */
    private final int line;
    /**
     * TokenType of this Token
     */
    private final TokenType type;
    /**
     * String that matches this Token's type
     */
    private final String value;

    /**
     * Token constructor
     *
     * @param line Line on which this Token is located
     * @param type TokenType of this Token
     * @param value String that matches this Token's type
     */
    public Token(int line, TokenType type, String value) {
        this.line = line;
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Token(" + line + "," + type + "," + value.replaceAll("\n", "\\\\n") + ")";
    }
}