package me.itzisonn_.meazy.lexer;

import lombok.Getter;

import java.util.regex.Pattern;

@Getter
public class TokenType {
    private final String id;
    private final Pattern pattern;
    private final boolean shouldSkip;

    public TokenType(String id, String regex, boolean shouldSkip) {
        this.id = id;
        if (regex == null) pattern = null;
        else this.pattern = Pattern.compile(regex, Pattern.DOTALL);
        this.shouldSkip = shouldSkip;
    }

    @Override
    public String toString() {
        return "TokenType(" + id + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TokenType other)) {
            return false;
        }
        String this$id = getId();
        String other$id = other.getId();
        if (this$id == null) return other$id == null;
        else return this$id.equals(other$id);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = result * 59 + (isShouldSkip() ? 79 : 97);
        Object pattern = getPattern();
        result = result * 59 + (pattern == null ? 43 : pattern.hashCode());
        return result;
    }
}