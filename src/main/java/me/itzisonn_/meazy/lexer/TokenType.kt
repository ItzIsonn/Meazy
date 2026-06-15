package me.itzisonn_.meazy.lexer

import me.itzisonn_.meazy.MeazyMain
import me.itzisonn_.meazy.registry.Registries
import java.util.regex.Pattern

/**
 * Represents type of token
 *
 * @param id         Id
 * @param pattern    Pattern that is used to match this token type
 * @param shouldSkip Should [Token]s with this type be skipped (not added in list
 * @param canMatch   Function that checks whether given string can match this token type
 *
 * @throws IllegalArgumentException If given id doesn't match [MeazyMain.IDENTIFIER_REGEX]
 * 
 * @see Registries.TOKEN_TYPES
 */
class TokenType(
    val id: String,
    pattern: Pattern?,
    val shouldSkip: Boolean,
    val canMatch: (String) -> Boolean = { true }
) {
    /**
     * Pattern that is used to match this token type
     */
    val pattern: Pattern?

    init {
        var pattern = pattern
        require(id.matches(MeazyMain.IDENTIFIER_REGEX.toRegex())) { "Invalid id" }

        if (pattern != null && !pattern.pattern().startsWith("^")) {
            pattern = Pattern.compile("^(${pattern.pattern()})", pattern.flags())
        }

        this.pattern = pattern
    }

    /**
     * Constructor with regex that is compiled into pattern
     * 
     * @param id         Id
     * @param regex      Regex that is compiled into [Pattern]
     * @param shouldSkip Should [Token]s with this type be skipped (not added in list)
     * @throws IllegalArgumentException If given id doesn't match [MeazyMain.IDENTIFIER_REGEX]
     */
    constructor(
        id: String,
        regex: String?,
        shouldSkip: Boolean,
        canMatch: (String) -> Boolean = { true }
    ) : this(
        id,
        if (regex == null) null else Pattern.compile(regex, Pattern.DOTALL),
        shouldSkip,
        canMatch
    )



    override fun toString(): String {
        return "TokenType($id)"
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is TokenType) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}