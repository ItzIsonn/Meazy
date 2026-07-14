package me.itzisonn_.meazy.lexer

/**
 * Represents type of token
 *
 * @param id       Id
 * @param regex    Regex that is used to match this token type
 * @param behavior Behavior
 * @param canMatch Function that checks whether given string can match this token type
 * 
 * @see TokenTypes
 */
class TokenType(
    val id: String,
    regex: Regex?,
    val behavior: TokenBehavior = TokenBehavior.DEFAULT,
    val canMatch: (String) -> Boolean = { true }
) {
    /**
     * Regex that is used to match this token type
     */
    val regex: Regex?

    init {
        var regex = regex

        if (regex != null && !regex.pattern.startsWith("^")) {
            regex = Regex("^(${regex.pattern})", regex.options)
        }

        this.regex = regex
    }

    /**
     * Constructor with regex string that is compiled into [Regex]
     * 
     * @param id       Id
     * @param regex    Regex string that is compiled into [Regex]
     * @param behavior Behavior
     */
    constructor(
        id: String,
        regex: String?,
        behavior: TokenBehavior = TokenBehavior.DEFAULT,
        canMatch: (String) -> Boolean = { true }
    ) : this(
        id,
        if (regex == null) null else Regex(regex, RegexOption.DOT_MATCHES_ALL),
        behavior,
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