package me.itzisonn_.meazy.parser;

import lombok.Getter;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.lexer.TokenType;
import me.itzisonn_.meazy.lexer.TokenTypeSet;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.registry.RegistryEntry;
import me.itzisonn_.registry.RegistryIdentifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Is used to store and parse tokens
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
     * Resets tokens to null and pos to 0
     * <p>
     * <i>Recommended to not use this method</i>
     */
    public static void reset() {
        tokens = null;
        pos = 0;
    }

    /**
     * Updates {@link Parser#tokens}
     * <p>
     * <i>Don't use this method because it's called once at parse startup</i>
     *
     * @param tokens New tokens list
     */
    public static void setTokens(List<Token> tokens) throws NullPointerException {
        if (tokens == null) throw new NullPointerException("Tokens can't be null");
        Parser.tokens = tokens;
    }

    /**
     * @return Copy of tokens list
     */
    public static List<Token> getTokens() {
        return new ArrayList<>(tokens);
    }

    /**
     * @return Token at {@link Parser#pos} in {@link Parser#tokens}
     */
    public static Token getCurrent() {
        return tokens.get(pos);
    }

    /**
     * Returns token at current position and increments position by 1
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
     * @param message Exception's message
     * @return Token at {@link Parser#pos} in {@link Parser#tokens}
     *
     * @throws UnexpectedTokenException If token's type doesn't match required
     */
    public static Token getCurrentAndNext(TokenType tokenType, String message) throws NullPointerException, UnexpectedTokenException {
        if (tokenType == null) throw new NullPointerException("TokenType can't be null");
        if (message == null) throw new NullPointerException("Message can't be null");

        if (!getCurrent().getType().equals(tokenType)) throw new UnexpectedTokenException(message, getCurrent().getLine());
        return getCurrentAndNext();
    }

    /**
     * Returns token at current position and increments position by 1
     *
     * @param tokenTypeSet Required TokenTypeSet
     * @param message Exception's message
     * @return Token at {@link Parser#pos} in {@link Parser#tokens}
     *
     * @throws UnexpectedTokenException If tokenTypeSet doesn't contain current token's type
     */
    public static Token getCurrentAndNext(TokenTypeSet tokenTypeSet, String message) throws NullPointerException, UnexpectedTokenException {
        if (tokenTypeSet == null) throw new NullPointerException("TokenTypeSet can't be null");
        if (message == null) throw new NullPointerException("Message can't be null");

        if (!tokenTypeSet.getTokenTypes().contains(getCurrent().getType())) throw new UnexpectedTokenException(message, getCurrent().getLine());
        return getCurrentAndNext();
    }

    /**
     * Skips all {@link TokenTypes#NEW_LINE()} tokens
     */
    public static void moveOverOptionalNewLines() {
        while (getCurrent().getType().equals(TokenTypes.NEW_LINE())) pos++;
    }

    /**
     * Checks current line for presence of token with given tokenType
     *
     * @param tokenType Required TokenType
     * @return Whether current line has token with given tokenType
     *
     * @throws NullPointerException If given tokenType is null
     */
    public static boolean currentLineHasToken(TokenType tokenType) throws NullPointerException {
        if (tokenType == null) throw new NullPointerException("TokenType can't be null");

        for (int i = pos; i < tokens.size(); i++) {
            TokenType current = tokens.get(i).getType();
            if (current.equals(TokenTypes.NEW_LINE())) return false;
            if (current.equals(tokenType)) return true;
        }

        return false;
    }



    /**
     * Executes ParsingFunction with given id
     *
     * @param id Id of ParsingFunction
     * @param extra Extra info
     * @return Parsed statement
     *
     * @throws NullPointerException If either id or extra is null
     * @throws IllegalArgumentException If can't find ParsingFunction with given id
     */
    public static Statement parse(RegistryIdentifier id, Object... extra) throws NullPointerException, IllegalArgumentException {
        if (id == null) throw new NullPointerException("Id can't be null");
        if (extra == null) throw new NullPointerException("Extra can't be null");

        ParsingFunction<? extends Statement> parsingFunction = getParsingFunctionOrNull(id);
        if (parsingFunction == null) throw new IllegalArgumentException("Can't find ParsingFunction with id " + id);

        return parsingFunction.parse(extra);
    }

    /**
     * Executes ParsingFunction with given id
     *
     * @param id Id of ParsingFunction
     * @param cls Required returned statement's class
     * @param extra Extra info
     * @param <T> Returned statement's type
     * @return Parsed statement
     *
     * @throws NullPointerException If either id, cls or extra is null
     * @throws IllegalArgumentException If can't find ParsingFunction with given id
     *                                  or return type of ParsingFunction doesn't match requested
     */
    @SuppressWarnings("unchecked")
    public static <T extends Statement> T parse(RegistryIdentifier id, Class<T> cls, Object... extra) throws NullPointerException, IllegalArgumentException {
        if (cls == null) throw new NullPointerException("Class can't be null");

        Statement statement = parse(id, extra);
        if (!cls.isInstance(statement)) throw new IllegalArgumentException("Return type of ParsingFunction with id " + id + " doesn't match requested (" + cls.getName() + ")");

        return (T) statement;
    }

    /**
     * Executes ParsingFunction after ParsingFunction with given id
     *
     * @param id Id of ParsingFunction
     * @param extra Extra info
     * @return Parsed statement
     *
     * @throws NullPointerException If either id or extra is null
     * @throws IllegalArgumentException If can't find ParsingFunction with given id
     */
    public static Statement parseAfter(RegistryIdentifier id, Object... extra) throws NullPointerException, IllegalArgumentException {
        if (id == null) throw new NullPointerException("Id can't be null");
        if (extra == null) throw new NullPointerException("Extra can't be null");

        ParsingFunction<? extends Statement> parsingFunction = getParsingFunctionAfterOrNull(id);
        if (parsingFunction == null) throw new IllegalArgumentException("Can't find ParsingFunction with id " + id);

        return parsingFunction.parse(extra);
    }

    /**
     * Executes ParsingFunction after ParsingFunction with given id
     *
     * @param id Id of ParsingFunction
     * @param cls Required returned statement's class
     * @param extra Extra info
     * @param <T> Returned statement's type
     * @return Parsed statement
     *
     * @throws NullPointerException If either id, cls or extra is null
     * @throws IllegalArgumentException If can't find ParsingFunction with given id
     *                                  or return type of ParsingFunction doesn't match requested
     */
    @SuppressWarnings("unchecked")
    public static <T extends Statement> T parseAfter(RegistryIdentifier id, Class<T> cls, Object... extra) throws NullPointerException, IllegalArgumentException {
        if (cls == null) throw new NullPointerException("Class can't be null");

        Statement statement = parseAfter(id, extra);
        if (!cls.isInstance(statement)) throw new IllegalArgumentException("Return type of ParsingFunction with id " + id + " doesn't match requested (" + cls.getName() + ")");

        return (T) statement;
    }



    /**
     * Finds ParsingFunction with given id
     *
     * @param id Id
     * @return ParsingFunction with give id or null
     *
     * @throws NullPointerException If given id is null
     */
    private static ParsingFunction<? extends Statement> getParsingFunctionOrNull(RegistryIdentifier id) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");

        RegistryEntry<ParsingFunction<? extends Statement>> entry = Registries.PARSING_FUNCTIONS.getEntry(id);
        if (entry == null) return null;

        return entry.getValue();
    }

    /**
     * Finds ParsingFunction after ParsingFunction with given id
     *
     * @param id Id
     * @return ParsingFunction after ParsingFunction with given id or null
     *
     * @throws NullPointerException If given id is null
     */
    private static ParsingFunction<? extends Statement> getParsingFunctionAfterOrNull(RegistryIdentifier id) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");

        RegistryEntry<ParsingFunction<? extends Statement>> entry = Registries.PARSING_FUNCTIONS.getEntryAfter(id);
        if (entry == null) return null;

        return entry.getValue();
    }
}