package me.itzisonn_.meazy.parser;

import lombok.Getter;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.lexer.TokenType;
import me.itzisonn_.meazy.lexer.TokenTypeSet;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryEntry;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser used to parse tokens
 *
 * @see Registries#PARSING_FUNCTIONS
 */
public final class Parser {
    private static List<Token> tokens;

    /**
     * Position of current element in {@link Parser#tokens}
     */
    @Getter
    private static int pos = 0;

    private Parser() {}



    /**
     * Resets all data
     */
    public static void reset() {
        tokens = null;
        pos = 0;
    }

    /**
     * @return Copy of tokens list
     */
    public static List<Token> getTokens() {
        return new ArrayList<>(tokens);
    }

    /**
     * Updates {@link Parser#tokens}
     * <p>
     * <i>Don't use this method because it's called once at parse startup</i>
     *
     * @param tokens New tokens list
     */
    public static void setTokens(List<Token> tokens) {
        Parser.tokens = tokens;
    }

    /**
     * @return Token at {@link Parser#pos} in {@link Parser#tokens}
     */
    public static Token getCurrent() {
        return tokens.get(pos);
    }

    /**
     * Returns token at current position and increments position by 1
     *
     * @return Token at {@link Parser#pos} in {@link Parser#tokens}
     */
    public static Token getCurrentAndNext() {
        Token token = getCurrent();
        pos++;
        return token;
    }

    /**
     * Returns token at current position increments position by 1
     *
     * @param tokenType Required TokenType
     * @param e Exception's message
     * @return Token at {@link Parser#pos} in {@link Parser#tokens}
     *
     * @throws UnexpectedTokenException If token's type doesn't match required
     */
    public static Token getCurrentAndNext(TokenType tokenType, String e) throws UnexpectedTokenException {
        if (!getCurrent().getType().equals(tokenType)) throw new UnexpectedTokenException(e, getCurrent().getLine());
        return getCurrentAndNext();
    }

    /**
     * Returns token at current position and increments position by 1
     *
     * @param tokenTypeSet Required TokenTypeSet
     * @param e Exception's message
     * @return Token at {@link Parser#pos} in {@link Parser#tokens}
     *
     * @throws UnexpectedTokenException If token's type doesn't match required
     */
    public static Token getCurrentAndNext(TokenTypeSet tokenTypeSet, String e) throws UnexpectedTokenException {
        if (!tokenTypeSet.getTokenTypes().contains(getCurrent().getType())) throw new UnexpectedTokenException(e, getCurrent().getLine());
        return getCurrentAndNext();
    }

    /**
     * While current token's type is {@link TokenTypes#NEW_LINE()}, increments position by 1
     */
    public static void moveOverOptionalNewLines() {
        while (getCurrent().getType().equals(TokenTypes.NEW_LINE())) pos++;
    }

    public static boolean currentLineHasToken(TokenType tokenType) {
        for (int i = pos; i < tokens.size(); i++) {
            TokenType current = tokens.get(i).getType();
            if (current.equals(TokenTypes.NEW_LINE())) return false;
            if (current.equals(tokenType)) return true;
        }

        return false;
    }



    /**
     * Executes ParsingFunction with given identifier
     *
     * @param identifier Identifier of ParsingFunction
     * @param extra Extra info
     * @return Parsed statement
     *
     * @throws NullPointerException If identifier is null
     * @throws IllegalArgumentException If can't find ParsingFunction with given identifier
     */
    public static Statement parse(RegistryIdentifier identifier, Object... extra) throws NullPointerException, IllegalArgumentException {
        if (identifier == null) throw new NullPointerException("Identifier can't be null!");

        ParsingFunction<? extends Statement> parsingFunction = getParsingFunctionOrNull(identifier);
        if (parsingFunction == null) throw new IllegalArgumentException("Can't find ParsingFunction with identifier " + identifier);

        return parsingFunction.parse(extra);
    }

    /**
     * Executes ParsingFunction with given identifier
     *
     * @param identifier Identifier of ParsingFunction
     * @param cls Required returned statement's class
     * @param extra Extra info
     * @param <T> Returned statement's type
     * @return Parsed statement
     *
     * @throws NullPointerException If identifier or cls is null
     * @throws IllegalArgumentException If can't find ParsingFunction with given identifier
     *                                  or return type of ParsingFunction doesn't match requested
     */
    @SuppressWarnings("unchecked")
    public static <T extends Statement> T parse(RegistryIdentifier identifier, Class<T> cls, Object... extra) throws NullPointerException, IllegalArgumentException {
        if (cls == null) throw new NullPointerException("Class can't be null!");

        Statement statement = parse(identifier, extra);
        if (!cls.isInstance(statement)) throw new IllegalArgumentException("Return type of ParsingFunction with identifier " + identifier + " doesn't match requested (" + cls.getName() + ")");

        return (T) statement;
    }

    /**
     * Executes ParsingFunction after ParsingFunction with given identifier
     *
     * @param identifier Identifier of ParsingFunction
     * @param extra Extra info
     * @return Parsed statement
     *
     * @throws NullPointerException If identifier is null
     * @throws IllegalArgumentException If can't find ParsingFunction with given identifier
     */
    public static Statement parseAfter(RegistryIdentifier identifier, Object... extra) throws NullPointerException, IllegalArgumentException {
        if (identifier == null) throw new NullPointerException("Identifier can't be null!");

        ParsingFunction<? extends Statement> parsingFunction = getParsingFunctionAfterOrNull(identifier);
        if (parsingFunction == null) throw new IllegalArgumentException("Can't find ParsingFunction with identifier " + identifier);

        return parsingFunction.parse(extra);
    }

    /**
     * Executes ParsingFunction after ParsingFunction with given identifier
     *
     * @param identifier Identifier of ParsingFunction
     * @param cls Required returned statement's class
     * @param extra Extra info
     * @param <T> Returned statement's type
     * @return Parsed statement
     *
     * @throws NullPointerException If identifier or cls is null
     * @throws IllegalArgumentException If can't find ParsingFunction with given identifier
     *                                  or return type of ParsingFunction doesn't match requested
     */
    @SuppressWarnings("unchecked")
    public static <T extends Statement> T parseAfter(RegistryIdentifier identifier, Class<T> cls, Object... extra) throws NullPointerException, IllegalArgumentException {
        if (cls == null) throw new NullPointerException("Class can't be null!");

        Statement statement = parseAfter(identifier, extra);
        if (!cls.isInstance(statement)) throw new IllegalArgumentException("Return type of ParsingFunction with identifier " + identifier + " doesn't match requested (" + cls.getName() + ")");

        return (T) statement;
    }

    /**
     * Finds ParsingFunction with given identifier
     *
     * @param identifier Identifier
     * @return ParsingFunction or null
     */
    private static ParsingFunction<? extends Statement> getParsingFunctionOrNull(RegistryIdentifier identifier) {
        RegistryEntry<ParsingFunction<? extends Statement>> entry = Registries.PARSING_FUNCTIONS.getEntry(identifier);
        if (entry == null) return null;
        return entry.getValue();
    }

    /**
     * Finds ParsingFunction after ParsingFunction with given identifier
     *
     * @param identifier Identifier
     * @return ParsingFunction or null
     */
    private static ParsingFunction<? extends Statement> getParsingFunctionAfterOrNull(RegistryIdentifier identifier) {
        RegistryEntry<ParsingFunction<? extends Statement>> entry = Registries.PARSING_FUNCTIONS.getEntryAfter(identifier);
        if (entry == null) return null;
        return entry.getValue();
    }
}