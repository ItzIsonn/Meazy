package me.itzisonn_.meazy.lexer;

import lombok.Getter;
import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.registry.Registries;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * TokenType
 *
 * @see Registries#TOKEN_TYPES
 */
@Getter
public class TokenType {
    /**
     * Id that matches {@link Utils#IDENTIFIER_REGEX}
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
     * Predicate that checks can string match this TokenType
     */
    private final Predicate<String> canMatch;

    /**
     * TokenType constructor
     *
     * @param id Id that matches {@link Utils#IDENTIFIER_REGEX}
     * @param regex Regex that is converted into {@link Pattern}
     * @param shouldSkip Should {@link Token}s with this type be skipped (not added in list)
     *
     * @throws NullPointerException If given id is null
     * @throws IllegalArgumentException If given id doesn't match {@link Utils#IDENTIFIER_REGEX}
     */
    public TokenType(String id, String regex, boolean shouldSkip) throws NullPointerException, IllegalArgumentException {
        this(id, regex, shouldSkip, s -> true);
    }

    /**
     * TokenType constructor
     *
     * @param id Id that matches {@link Utils#IDENTIFIER_REGEX}
     * @param regex Regex that is converted into {@link Pattern}
     * @param shouldSkip Should {@link Token}s with this type be skipped (not added in list)
     * @param canMatch Predicate that checks can string match this TokenType
     *
     * @throws NullPointerException If given id is null
     * @throws IllegalArgumentException If given id doesn't match {@link Utils#IDENTIFIER_REGEX}
     */
    public TokenType(String id, String regex, boolean shouldSkip, Predicate<String> canMatch) throws NullPointerException, IllegalArgumentException {
        this(id, regex == null ? null : Pattern.compile(regex, Pattern.DOTALL), shouldSkip, canMatch);
    }

    /**
     * TokenType constructor
     *
     * @param id Id that matches {@link Utils#IDENTIFIER_REGEX}
     * @param pattern Pattern that is used to match tokens
     * @param shouldSkip Should {@link Token}s with this type be skipped (not added in list)
     * @param canMatch Predicate that checks can string match this TokenType
     *
     * @throws NullPointerException If given id is null
     * @throws IllegalArgumentException If given id doesn't match {@link Utils#IDENTIFIER_REGEX}
     */
    public TokenType(String id, Pattern pattern, boolean shouldSkip, Predicate<String> canMatch) throws NullPointerException, IllegalArgumentException {
        if (id == null) throw new NullPointerException("Id can't be null");
        if (!id.matches(Utils.IDENTIFIER_REGEX)) throw new IllegalArgumentException("Invalid id");

        this.id = id;
        this.pattern = pattern;
        this.shouldSkip = shouldSkip;
        this.canMatch = canMatch == null ? s -> true : canMatch;
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
        String thisId = getId();
        String otherId = other.getId();
        if (thisId == null) return otherId == null;
        else return thisId.equals(otherId);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}