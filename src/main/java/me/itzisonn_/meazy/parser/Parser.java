package me.itzisonn_.meazy.parser;

import lombok.Getter;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.lexer.TokenType;
import me.itzisonn_.meazy.lexer.TokenTypeSet;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.registry.RegistryEntry;
import me.itzisonn_.registry.RegistryIdentifier;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Is used to store and parse tokens
 * @see Registries#PARSING_FUNCTIONS
 */
@NullMarked
public class Parser {
    private final ParsingContext context;
    private final List<Token> tokens;

    /**
     * Position of current element in {@link Parser#tokens}
     */
    @Getter
    private int pos = 0;

    /**
     *
     * @param context Parsing context
     * @param tokens List of tokens
     */
    public Parser(ParsingContext context, List<Token> tokens) {
        this.context = context;
        this.tokens = tokens;
    }



    /**
     * @return Copy of tokens list
     */
    public List<Token> getTokens() {
        return new ArrayList<>(tokens);
    }

    /**
     * @return Token at {@link Parser#pos} in {@link Parser#tokens}
     */
    public Token getCurrent() {
        return tokens.get(pos);
    }



    /**
     * Increments position by 1
     */
    public void next() {
        pos++;
    }

    /**
     * Increments position by 1
     *
     * @param tokenType Required TokenType
     * @param text Exception's text=
     *
     * @throws UnexpectedTokenException If token's type doesn't match required
     */
    public void next(TokenType tokenType, Text text) throws UnexpectedTokenException {
        if (!getCurrent().getType().equals(tokenType)) {
            throw new UnexpectedTokenException(getCurrent().getLine(), text);
        }

        next();
    }

    /**
     * Increments position by 1
     *
     * @param tokenTypeSet Required TokenTypeSet
     * @param text Exception's text
     *
     * @throws UnexpectedTokenException If tokenTypeSet doesn't contain current token's type
     */
    public void next(TokenTypeSet tokenTypeSet, Text text) throws UnexpectedTokenException {
        if (!tokenTypeSet.getTokenTypes().contains(getCurrent().getType())) {
            throw new UnexpectedTokenException(getCurrent().getLine(), text);
        }

        next();
    }



    /**
     * Returns token at current position and increments position by 1
     * @return Token at {@link Parser#pos} in {@link Parser#tokens}
     */
    public Token getCurrentAndNext() {
        Token token = getCurrent();
        pos++;
        return token;
    }

    /**
     * Returns token at current position increments position by 1
     *
     * @param tokenType Required TokenType
     * @param text Exception's text
     * @return Token at {@link Parser#pos} in {@link Parser#tokens}
     *
     * @throws UnexpectedTokenException If token's type doesn't match required
     */
    public Token getCurrentAndNext(TokenType tokenType, Text text) throws UnexpectedTokenException {
        if (!getCurrent().getType().equals(tokenType)) {
            throw new UnexpectedTokenException(getCurrent().getLine(), text);
        }

        return getCurrentAndNext();
    }

    /**
     * Returns token at current position and increments position by 1
     *
     * @param tokenTypeSet Required TokenTypeSet
     * @param text Exception's text
     * @return Token at {@link Parser#pos} in {@link Parser#tokens}
     *
     * @throws UnexpectedTokenException If tokenTypeSet doesn't contain current token's type
     */
    public Token getCurrentAndNext(TokenTypeSet tokenTypeSet, Text text) throws UnexpectedTokenException {
        if (!tokenTypeSet.getTokenTypes().contains(getCurrent().getType())) {
            throw new UnexpectedTokenException(getCurrent().getLine(), text);
        }

        return getCurrentAndNext();
    }

    /**
     * Skips all {@link TokenTypes#NEW_LINE()} tokens
     */
    public void moveOverOptionalNewLines() {
        while (getCurrent().getType().equals(TokenTypes.NEW_LINE())) pos++;
    }

    /**
     * Checks current line for presence of token with given tokenType
     *
     * @param tokenType Required TokenType
     * @return Whether current line has token with given tokenType
     */
    public boolean currentLineHasToken(TokenType tokenType) {
        for (int i = pos; i < tokens.size(); i++) {
            TokenType current = tokens.get(i).getType();
            if (current.equals(TokenTypes.NEW_LINE())) return false;
            if (current.equals(tokenType)) return true;
        }

        return false;
    }

    /**
     * Checks current line for presence of token with type inside given tokenTypeSet
     *
     * @param tokenTypeSet Required TokenTypeSet
     * @return Whether current line has token with type inside given tokenTypeSet
     */
    public boolean currentLineHasToken(TokenTypeSet tokenTypeSet) {
        for (int i = pos; i < tokens.size(); i++) {
            TokenType current = tokens.get(i).getType();
            if (current.equals(TokenTypes.NEW_LINE())) return false;
            if (tokenTypeSet.contains(current)) return true;
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
     * @throws IllegalArgumentException When can't find ParsingFunction with given id
     */
    public Statement parse(RegistryIdentifier id, @Nullable Object... extra) throws IllegalArgumentException {
        ParsingFunction<? extends Statement> parsingFunction = getParsingFunctionOrNull(id);
        if (parsingFunction == null) throw new IllegalArgumentException("Can't find ParsingFunction with id " + id);
        return parsingFunction.parse(context, extra);
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
     * @throws IllegalArgumentException When can't find ParsingFunction with given id
     *                                  or return type of ParsingFunction doesn't match requested
     */
    @SuppressWarnings("unchecked")
    public <T extends Statement> T parse(RegistryIdentifier id, Class<T> cls, @Nullable Object... extra) throws IllegalArgumentException {
        Statement statement = parse(id, extra);

        if (!cls.isInstance(statement)) {
            throw new IllegalArgumentException("Return type of ParsingFunction with id " + id + " doesn't match requested (" + cls.getName() + ")");
        }

        return (T) statement;
    }

    /**
     * Executes ParsingFunction after ParsingFunction with given id
     *
     * @param id Id of ParsingFunction
     * @param extra Extra info
     * @return Parsed statement
     *
     * @throws IllegalArgumentException When can't find ParsingFunction with given id
     */
    public Statement parseAfter(RegistryIdentifier id, @Nullable Object... extra) throws IllegalArgumentException {
        ParsingFunction<? extends Statement> parsingFunction = getParsingFunctionAfterOrNull(id);
        if (parsingFunction == null) throw new IllegalArgumentException("Can't find ParsingFunction with id " + id);

        return parsingFunction.parse(context, extra);
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
     * @throws IllegalArgumentException When can't find ParsingFunction with given id
     *                                  or return type of ParsingFunction doesn't match requested
     */
    @SuppressWarnings("unchecked")
    public <T extends Statement> T parseAfter(RegistryIdentifier id, Class<T> cls, @Nullable Object... extra) throws IllegalArgumentException {
        Statement statement = parseAfter(id, extra);

        if (!cls.isInstance(statement)) {
            throw new IllegalArgumentException("Return type of ParsingFunction with id " + id + " doesn't match requested (" + cls.getName() + ")");
        }

        return (T) statement;
    }



    /**
     * Finds ParsingFunction with given id
     *
     * @param id Id
     * @return ParsingFunction with given id or null
     */
    @Nullable
    private ParsingFunction<? extends Statement> getParsingFunctionOrNull(RegistryIdentifier id) {
        RegistryEntry<ParsingFunction<? extends Statement>> entry = Registries.PARSING_FUNCTIONS.getEntry(id);
        if (entry == null) return null;
        return entry.getValue();
    }

    /**
     * Finds ParsingFunction after ParsingFunction with given id
     *
     * @param id Id
     * @return ParsingFunction after ParsingFunction with given id or null
     */
    @Nullable
    private ParsingFunction<? extends Statement> getParsingFunctionAfterOrNull(RegistryIdentifier id) {
        RegistryEntry<ParsingFunction<? extends Statement>> entry = Registries.PARSING_FUNCTIONS.getEntryAfter(id);
        if (entry == null) return null;

        return entry.getValue();
    }
}