package me.itzisonn_.meazy.parser;

import lombok.Getter;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.lexer.TokenType;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryEntry;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.util.ArrayList;

public final class Parser {
    private static ArrayList<Token> tokens;
    @Getter
    private static int pos = 0;

    private Parser() {}

    public static ArrayList<Token> getTokens() {
        return new ArrayList<>(tokens);
    }

    public static void setTokens(ArrayList<Token> tokens) {
        Parser.tokens = tokens;
    }

    public static Token getCurrent() {
        return tokens.get(pos);
    }

    public static Token getCurrentAndNext() {
        Token token = getCurrent();
        pos++;
        return token;
    }

    public static Token getCurrentAndNext(TokenType tokenType, String e) throws RuntimeException {
        if (!getCurrent().getType().equals(tokenType)) throw new UnexpectedTokenException(e, getCurrent().getLine());
        return getCurrentAndNext();
    }

    public static void moveOverOptionalNewLine() {
        if (getCurrent().getType().equals(TokenTypes.NEW_LINE())) getCurrentAndNext();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Statement> T parse(RegistryIdentifier identifier, Class<T> cls, Object... extra) {
        if (identifier == null) throw new NullPointerException("Identifier can't be null!");

        ParsingFunction<? extends Statement> parsingFunction = getParsingFunctionOrNull(identifier);
        if (parsingFunction == null) throw new IllegalArgumentException("Can't find ParsingFunction with identifier " + identifier);

        try {
            return ((ParsingFunction<T>) parsingFunction).parse(extra);
        }
        catch (ClassCastException ignore) {
            throw new IllegalArgumentException("Return value type of ParsingFunction with identifier " + identifier + " doesn't match with requested (" + cls.getName() + ")");
        }
    }

    public static Statement parse(RegistryIdentifier identifier, Object... extra) {
        if (identifier == null) throw new NullPointerException("Identifier can't be null!");

        ParsingFunction<? extends Statement> parsingFunction = getParsingFunctionOrNull(identifier);
        if (parsingFunction == null) throw new IllegalArgumentException("Can't find ParsingFunction with identifier " + identifier);

        return parsingFunction.parse(extra);
    }

    private static ParsingFunction<? extends Statement> getParsingFunctionOrNull(RegistryIdentifier identifier) {
        RegistryEntry<ParsingFunction<? extends Statement>> entry = Registries.PARSING_FUNCTIONS.getEntry(identifier);
        if (entry == null) return null;
        return entry.getValue();
    }
}