package me.itzisonn_.meazy.lexer;

import lombok.Getter;
import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.Registries;

import java.util.regex.Pattern;

/**
 * TokenType
 * @see Registries#TOKEN_TYPES
 */
@Getter
public class TokenType {
    /**
     * TokenType's id that matches {@link Utils#IDENTIFIER_REGEX}
     */
    private final String id;
    /**
     * Pattern that is used to match tokens
     */
    private final Pattern pattern;
    /**
     * Should {@link Token}s with this type be skipped (not added in list)
     */
    private final boolean shouldSkip;

    /**
     * TokenType constructor
     *
     * @param id TokenType's id that matches {@link Utils#IDENTIFIER_REGEX}
     * @param regex Regex that is converted into {@link Pattern}
     * @param shouldSkip Should {@link Token}s with this type be skipped (not added in list)
     *
     * @throws NullPointerException If given id is null
     * @throws IllegalArgumentException If given id doesn't match {@link Utils#IDENTIFIER_REGEX}
     */
    public TokenType(String id, String regex, boolean shouldSkip) throws NullPointerException, IllegalArgumentException {
        this(id, regex == null ? null : Pattern.compile(regex, Pattern.DOTALL), shouldSkip);
    }

    /**
     * TokenType constructor
     *
     * @param id TokenType's id that matches {@link Utils#IDENTIFIER_REGEX}
     * @param pattern Pattern that is used to match tokens
     * @param shouldSkip Should {@link Token}s with this type be skipped (not added in list)
     *
     * @throws NullPointerException If given id is null
     * @throws IllegalArgumentException If given id doesn't match {@link Utils#IDENTIFIER_REGEX}
     */
    public TokenType(String id, Pattern pattern, boolean shouldSkip) throws NullPointerException, IllegalArgumentException {
        if (id == null) throw new NullPointerException("Id can't be null");
        if (!id.matches(Utils.IDENTIFIER_REGEX)) throw new IllegalArgumentException("Invalid id");
        if (pattern != null && !pattern.pattern().startsWith("^")) {
            pattern = Pattern.compile("^(" + pattern.pattern() + ")", pattern.flags());
        }

        this.id = id;
        this.pattern = pattern;
        this.shouldSkip = shouldSkip;
    }

    /**
     * @param string String to check
     * @return Can given string match this TokenType
     */
    public boolean canMatch(String string) {
        return true;
    }


    @Override
    public String toString() {
        return "TokenType(" + id + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof TokenType other)) return false;

        String thisId = getId();
        String otherId = other.getId();
        if (thisId == null) return otherId == null;
        return thisId.equals(otherId);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}