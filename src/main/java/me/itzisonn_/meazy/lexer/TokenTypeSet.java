package me.itzisonn_.meazy.lexer;

import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

/**
 * TokenTypeSet represents {@link Set} of {@link TokenType}
 *
 * @see TokenType
 */
@EqualsAndHashCode
public class TokenTypeSet {
    private final Set<TokenType> tokenTypes;

    /**
     * TokenTypeSet constructor
     *
     * @param tokenTypes Set of TokenTypes
     *
     * @throws NullPointerException If given set is null
     */
    public TokenTypeSet(Set<TokenType> tokenTypes) throws NullPointerException {
        if (tokenTypes == null) throw new NullPointerException("TokenTypes can't be null");
        this.tokenTypes = new HashSet<>(tokenTypes);
    }

    /**
     * TokenTypeSet constructor
     *
     * @param tokenTypes Array of TokenTypes
     *
     * @throws NullPointerException If given array is null
     */
    public TokenTypeSet(TokenType... tokenTypes) throws NullPointerException {
        this(tokenTypes == null ? null : Set.of(tokenTypes));
    }

    /**
     * @return Copy of TokenType Set
     */
    public Set<TokenType> getTokenTypes() {
        return Set.copyOf(tokenTypes);
    }

    /**
     * @param tokenType TokenType
     * @return This TokenTypeSet to allow chaining
     */
    public TokenTypeSet add(TokenType tokenType) throws IllegalArgumentException {
        if (!tokenTypes.add(tokenType)) throw new IllegalArgumentException("Given TokenType has already been added to this set");
        return this;
    }

    /**
     * @param tokenType TokenType
     * @return Whether contains given TokenType
     */
    public boolean contains(TokenType tokenType) {
        return tokenTypes.contains(tokenType);
    }
}