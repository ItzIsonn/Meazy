package me.itzisonn_.meazy.lexer;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.registry.Registries;
import org.jspecify.annotations.NullMarked;

/**
 * TokenTypes registrar
 *
 * @see Registries#TOKEN_TYPES
 */
public final class TokenTypes {
    private TokenTypes() {}



    public static TokenType NEW_LINE() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("new_line")).getValue();
    }

    public static TokenType END_OF_FILE() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("end_of_file")).getValue();
    }



    public static TokenType REQUIRE() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("require")).getValue();
    }

    public static TokenType IMPORT() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("import")).getValue();
    }

    public static TokenType VARIABLE() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("variable")).getValue();
    }

    public static TokenType FUNCTION() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("function")).getValue();
    }

    public static TokenType CLASS() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("class")).getValue();
    }

    public static TokenType INTERFACE() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("interface")).getValue();
    }

    public static TokenType CONSTRUCTOR() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("constructor")).getValue();
    }

    public static TokenType BASE() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("base")).getValue();
    }

    public static TokenType IF() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("if")).getValue();
    }

    public static TokenType ELSE() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("else")).getValue();
    }

    public static TokenType FOR() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("for")).getValue();
    }

    public static TokenType IN() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("in")).getValue();
    }

    public static TokenType WHILE() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("while")).getValue();
    }

    public static TokenType RETURN() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("return")).getValue();
    }

    public static TokenType CONTINUE() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("continue")).getValue();
    }

    public static TokenType BREAK() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("break")).getValue();
    }

    public static TokenType IS() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("is")).getValue();
    }

    public static TokenType IS_LIKE() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("is_like")).getValue();
    }



    public static TokenType LEFT_PARENTHESIS() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("left_parenthesis")).getValue();
    }

    public static TokenType RIGHT_PARENTHESIS() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("right_parenthesis")).getValue();
    }

    public static TokenType LEFT_BRACE() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("left_brace")).getValue();
    }

    public static TokenType RIGHT_BRACE() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("right_brace")).getValue();
    }

    public static TokenType LEFT_BRACKET() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("left_bracket")).getValue();
    }

    public static TokenType RIGHT_BRACKET() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("right_bracket")).getValue();
    }

    public static TokenType COLON() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("colon")).getValue();
    }

    public static TokenType COMMA() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("comma")).getValue();
    }

    public static TokenType DOT() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("dot")).getValue();
    }

    public static TokenType QUESTION() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("question")).getValue();
    }

    public static TokenType QUESTION_DOT() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("question_dot")).getValue();
    }

    public static TokenType QUESTION_COLON() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("question_colon")).getValue();
    }

    public static TokenType ARROW() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("arrow")).getValue();
    }



    public static TokenType ASSIGN() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("assign")).getValue();
    }

    public static TokenType MINUS() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("minus")).getValue();
    }

    public static TokenType POWER() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("power")).getValue();
    }



    public static TokenType AND() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("and")).getValue();
    }

    public static TokenType OR() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("or")).getValue();
    }

    public static TokenType INVERSION() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("inversion")).getValue();
    }



    public static TokenType NULL() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("null")).getValue();
    }

    public static TokenType NUMBER() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("number")).getValue();
    }

    public static TokenType STRING() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("string")).getValue();
    }

    public static TokenType BOOLEAN() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("boolean")).getValue();
    }

    public static TokenType THIS() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("this")).getValue();
    }

    public static TokenType ID() {
        return Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier("id")).getValue();
    }



    @NativeCanMatch
    @NullMarked
    public static boolean canMatchId(String string) {
        for (TokenType tokenType : TokenTypeSets.KEYWORDS().getTokenTypes()) {
            if (tokenType.getPattern() != null && tokenType.getPattern().matcher(string).matches()) return false;
        }
        return true;
    }
}
