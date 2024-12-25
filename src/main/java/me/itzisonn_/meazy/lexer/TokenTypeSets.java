package me.itzisonn_.meazy.lexer;

public class TokenTypeSets {
    public static final TokenTypeSet KEYWORDS = new TokenTypeSet(
            TokenTypes.VARIABLE(),
            TokenTypes.FUNCTION(),
            TokenTypes.CLASS(),
            TokenTypes.CONSTRUCTOR(),
            TokenTypes.NEW(),
            TokenTypes.PRIVATE(),
            TokenTypes.SHARED(),
            TokenTypes.IF(),
            TokenTypes.ELSE(),
            TokenTypes.FOR(),
            TokenTypes.IN(),
            TokenTypes.WHILE(),
            TokenTypes.RETURN(),
            TokenTypes.CONTINUE(),
            TokenTypes.BREAK(),
            TokenTypes.NULL(),
            TokenTypes.BOOLEAN());

    public static final TokenTypeSet OPERATOR_ASSIGN = new TokenTypeSet(
            TokenTypes.PLUS_ASSIGN(),
            TokenTypes.MINUS_ASSIGN(),
            TokenTypes.MULTIPLY_ASSIGN(),
            TokenTypes.DIVIDE_ASSIGN(),
            TokenTypes.PERCENT_ASSIGN(),
            TokenTypes.POWER_ASSIGN());

    public static final TokenTypeSet OPERATOR_POSTFIX = new TokenTypeSet(
            TokenTypes.DOUBLE_PLUS(),
            TokenTypes.DOUBLE_MINUS());

    public static final TokenTypeSet ACCESS_MODIFIERS = new TokenTypeSet(
            TokenTypes.PRIVATE(),
            TokenTypes.SHARED());
}
