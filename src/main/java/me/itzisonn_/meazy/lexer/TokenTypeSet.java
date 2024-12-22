package me.itzisonn_.meazy.lexer;

import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode
public class TokenTypeSet {
    private final Set<TokenType> tokenTypes;

    public TokenTypeSet(Set<TokenType> tokenTypes) {
        this.tokenTypes = tokenTypes;
    }

    public TokenTypeSet(TokenType... tokenTypes) {
        this.tokenTypes = Set.of(tokenTypes);
    }

    public Set<TokenType> getTokenTypes() {
        return Set.copyOf(tokenTypes);
    }

    public boolean contains(TokenType tokenType) {
        return tokenTypes.contains(tokenType);
    }
}