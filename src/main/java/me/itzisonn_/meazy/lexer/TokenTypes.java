package me.itzisonn_.meazy.lexer;

import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

public class TokenTypes {
    public static final TokenType VARIABLE = register("variable", new TokenType("var|val", false));
    public static final TokenType FUNCTION = register("function", new TokenType("fn|func|function", false));
    public static final TokenType CLASS = register("class", new TokenType("class", false));
    public static final TokenType CONSTRUCTOR = register("constructor", new TokenType("constructor", false));
    public static final TokenType NEW = register("new", new TokenType("new", false));
    public static final TokenType PRIVATE = register("private", new TokenType("private", false));
    public static final TokenType SHARED = register("shared", new TokenType("shared", false));
    public static final TokenType IF = register("if", new TokenType("if", false));
    public static final TokenType ELSE = register("else", new TokenType("else", false));
    public static final TokenType FOR = register("for", new TokenType("for", false));
    public static final TokenType WHILE = register("while", new TokenType("while", false));
    public static final TokenType RETURN = register("return", new TokenType("return", false));
    public static final TokenType CONTINUE = register("continue", new TokenType("continue", false));
    public static final TokenType BREAK = register("break", new TokenType("break", false));

    public static final TokenType NEW_LINE = register("new_line", new TokenType("\n", false));
    public static final TokenType COMMENT = register("comment", new TokenType("\\/\\/[^\n]*", true));
    public static final TokenType MULTI_LINE_COMMENT = register("multi_line_comment", new TokenType("\\/\\*(?:(?!\\*\\/).)*\\*\\/", true));
    public static final TokenType WHITE_SPACE = register("white_space", new TokenType("(?!\n)\\s", true));
    public static final TokenType END_OF_FILE = register("end_of_file", new TokenType(null, false));

    public static final TokenType LEFT_PAREN = register("left_paren", new TokenType("\\(", false));
    public static final TokenType RIGHT_PAREN = register("right_paren", new TokenType("\\)", false));
    public static final TokenType LEFT_BRACE = register("left_brace", new TokenType("\\{", false));
    public static final TokenType RIGHT_BRACE = register("right_brace", new TokenType("\\}", false));
    public static final TokenType COLON = register("colon", new TokenType(":", false));
    public static final TokenType SEMICOLON = register("semicolon", new TokenType(";", false));
    public static final TokenType COMMA = register("comma", new TokenType(",", false));
    public static final TokenType DOT = register("dot", new TokenType("\\.", false));

    public static final TokenType ASSIGN = register("assign", new TokenType("=", false));
    public static final TokenType PLUS   = register("plus", new TokenType("\\+", false));
    public static final TokenType MINUS = register("minus", new TokenType("-", false));
    public static final TokenType MULTIPLY = register("multiply", new TokenType("\\*", false));
    public static final TokenType DIVIDE = register("divide", new TokenType("/", false));
    public static final TokenType PERCENT = register("percent", new TokenType("%", false));
    public static final TokenType POWER = register("power", new TokenType("\\^", false));
    public static final TokenType PLUS_ASSIGN = register("plus_assign", new TokenType("\\+=", false));
    public static final TokenType MINUS_ASSIGN = register("minus_assign", new TokenType("-=", false));
    public static final TokenType MULTIPLY_ASSIGN = register("multiply_assign", new TokenType("\\*=", false));
    public static final TokenType DIVIDE_ASSIGN = register("divide_assign", new TokenType("/=", false));
    public static final TokenType PERCENT_ASSIGN = register("percent_assign", new TokenType("%=", false));
    public static final TokenType POWER_ASSIGN = register("power_assign", new TokenType("\\^=", false));
    public static final TokenType DOUBLE_PLUS = register("double_plus", new TokenType("\\+\\+", false));
    public static final TokenType DOUBLE_MINUS = register("double_minus", new TokenType("--", false));
    public static final TokenType AND = register("and", new TokenType("&&", false));
    public static final TokenType OR = register("or", new TokenType("\\|\\|", false));
    public static final TokenType EQUALS = register("equals", new TokenType("==", false));
    public static final TokenType NOT_EQUALS = register("not_equals", new TokenType("!=", false));
    public static final TokenType GREATER = register("greater", new TokenType(">", false));
    public static final TokenType GREATER_OR_EQUALS = register("greater_or_equals", new TokenType(">=", false));
    public static final TokenType LESS = register("less", new TokenType("<", false));
    public static final TokenType LESS_OR_EQUALS = register("less_or_equals", new TokenType("<=", false));

    public static final TokenType ID;
    public static final TokenType NULL = register("null", new TokenType("null", false));
    public static final TokenType NUMBER = register("number", new TokenType("(-?)(0|([1-9][0-9]*))(\\.[0-9]+)?", false));
    public static final TokenType STRING = register("string", new TokenType("\"[^\"]*\"", false));
    public static final TokenType BOOLEAN = register("boolean", new TokenType("true|false", false));

    static {
        StringBuilder keywords = new StringBuilder();
        for (TokenType tokenType : TokenTypeSets.KEYWORDS.getTokenTypes()) {
            keywords.append("(?<!").append(tokenType.getRegex()).append(")");
        }
        ID = register("id", new TokenType(Utils.IDENTIFIER_REGEX + keywords, false));
    }



    private static TokenType register(String id, TokenType tokenType) {
        return Registries.TOKEN_TYPE.register(RegistryIdentifier.ofDefault(id), tokenType);
    }

    public static void INIT() {}
}
