package me.itzisonn_.meazy.lexer;

import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents {@link Set} of {@link TokenType}
 * @see TokenType
 */
@EqualsAndHashCode
public class TokenTypeSet {
    private final Set<TokenType> tokenTypes;

    /**
     * Main constructor
     * @param tokenTypes Set of TokenTypes
     * @throws NullPointerException If given set is null
     */
    public TokenTypeSet(Set<TokenType> tokenTypes) throws NullPointerException {
        if (tokenTypes == null) throw new NullPointerException("TokenTypes can't be null");
        this.tokenTypes = new HashSet<>(tokenTypes);
    }

    /**
     * Constructor with array of tokenTypes
     * @param tokenTypes Array of TokenTypes
     *
     * @throws NullPointerException If given array is null
     * @throws IllegalArgumentException If given array contains duplicate elements
     */
    public TokenTypeSet(TokenType... tokenTypes) throws NullPointerException, IllegalArgumentException {
        this(tokenTypes == null ? null : Set.of(tokenTypes));
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
        if (!tokenTypes.add(tokenType)) throw new IllegalArgumentException("TokenType has already been added to this set");
        return this;
    }

    /**
     * @param tokenType TokenType
     * @return Whether this token type set contains given tokenType
     */
    public boolean contains(TokenType tokenType) {
        return tokenTypes.contains(tokenType);
    }
}