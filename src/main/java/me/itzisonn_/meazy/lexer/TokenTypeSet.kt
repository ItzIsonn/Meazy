package me.itzisonn_.meazy.lexer;

import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents {@link Set} of {@link TokenType}
 *
 * @see TokenType
 */
@NullMarked
public class TokenTypeSet {
    private final String id;
    private final Set<TokenType> tokenTypes;

    /**
     * Main constructor
     *
     * @param id         Id
     * @param tokenTypes Set of TokenTypes
     */
    public TokenTypeSet(String id, Set<TokenType> tokenTypes) {
        this.id = id;
        this.tokenTypes = new HashSet<>(tokenTypes);
    }

    /**
     * Constructor with array of tokenTypes
     *
     * @param id         Id
     * @param tokenTypes Array of TokenTypes
     * @throws IllegalArgumentException If given array contains duplicate elements
     */
    public TokenTypeSet(String id, TokenType... tokenTypes) {
        this(id, Set.of(tokenTypes));
    }

    /**
     * @return Copy of this token type set
     */
    public Set<TokenType> getTokenTypes() {
        return Set.copyOf(tokenTypes);
    }

    /**
     * @param tokenType TokenType
     * @return This token type set to allow chaining
     */
    public TokenTypeSet add(TokenType tokenType) throws IllegalArgumentException {
        if (!tokenTypes.add(tokenType))
            throw new IllegalArgumentException("TokenType has already been added to this set");
        return this;
    }

    /**
     * @param tokenType TokenType
     * @return Whether this token type set contains given tokenType
     */
    public boolean contains(TokenType tokenType) {
        return tokenTypes.contains(tokenType);
    }

    public String getId() {
        return this.id;
    }
}