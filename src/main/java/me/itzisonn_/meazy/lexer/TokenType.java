package me.itzisonn_.meazy.lexer;

import lombok.Getter;

@Getter
public class TokenType {
    private final String id;
    private final String regex;
    private final boolean shouldSkip;

    public TokenType(String id, String regex, boolean shouldSkip) {
        this.id = id;
        this.regex = regex;
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
        else if (!(o instanceof TokenType other)) {
            return false;
        }
        else {
            Object this$id = getId();
            Object other$id = other.getId();
            if (this$id == null) {
                return other$id == null;
            }
            else return this$id.equals(other$id);
        }
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = result * 59 + (isShouldSkip() ? 79 : 97);
        Object regex = getRegex();
        result = result * 59 + (regex == null ? 43 : regex.hashCode());
        return result;
    }
}