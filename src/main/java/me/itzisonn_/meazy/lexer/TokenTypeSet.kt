package me.itzisonn_.meazy.lexer

/**
 * Represents [Set] of [TokenType]
 * 
 * @see TokenType
 */
class TokenTypeSet(val id: String, tokenTypes: Set<TokenType>) {
    private val tokenTypes = tokenTypes.toMutableSet()

    /**
     * Constructor with array of tokenTypes
     * 
     * @param id         Id
     * @param tokenTypes Array of TokenTypes
     * @throws IllegalArgumentException If given array contains duplicate elements
     */
    constructor(id: String, vararg tokenTypes: TokenType) : this(id, setOf(*tokenTypes))

    /**
     * @return Copy of this token type set
     */
    fun getTokenTypes(): Set<TokenType> {
        return tokenTypes.toSet()
    }

    /**
     * @param tokenType TokenType
     * @return This token type set to allow chaining
     */
    fun add(tokenType: TokenType): TokenTypeSet {
        require(tokenTypes.add(tokenType)) { "TokenType has already been added to this set" }
        return this
    }

    /**
     * @param tokenType TokenType
     * @return Whether this token type set contains given tokenType
     */
    fun contains(tokenType: TokenType): Boolean {
        return tokenTypes.contains(tokenType)
    }
}