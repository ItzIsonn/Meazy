package me.itzisonn_.meazy.lexer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryEntry;

@Getter
@EqualsAndHashCode
public class TokenType {
    private final String regex;
    private final boolean shouldSkip;

    public TokenType(String regex, boolean shouldSkip) {
        this.regex = regex;
        this.shouldSkip = shouldSkip;
    }

    @Override
    public String toString() {
        RegistryEntry<TokenType> entry = Registries.TOKEN_TYPE.getEntry(this);
        if (entry != null) return entry.getId().toString();
        return super.toString();
    }
}