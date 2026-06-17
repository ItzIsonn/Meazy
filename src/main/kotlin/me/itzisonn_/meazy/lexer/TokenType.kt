package me.itzisonn_.meazy.lexer

import me.itzisonn_.meazy.MeazyMain

/**
 * Represents type of token
 *
 * @param id         Id
 * @param regex      Regex that is used to match this token type
 * @param shouldSkip Should [Token]s with this type be skipped (not added in list
 * @param canMatch   Function that checks whether given string can match this token type
 *
 * @throws IllegalArgumentException If given id doesn't match [MeazyMain.IDENTIFIER_REGEX]
 * 
 * @see TokenTypes
 */
class TokenType(
    val id: String,
    regex: Regex?,
    val shouldSkip: Boolean,
    val canMatch: (String) -> Boolean = { true }
) {
    /**
     * Regex that is used to match this token type
     */
    val regex: Regex?

    init {
        var regex = regex
        require(id.matches(MeazyMain.IDENTIFIER_REGEX.toRegex())) { "Invalid id" }

        if (regex != null && !regex.pattern.startsWith("^")) {
            regex = Regex("^(${regex.pattern})", regex.options)
        }

        this.regex = regex
    }

    /**
     * Constructor with regex string that is compiled into [Regex]
     * 
     * @param id         Id
     * @param regex      Regex string that is compiled into [Regex]
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
        if (regex == null) null else Regex(regex, RegexOption.DOT_MATCHES_ALL),
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