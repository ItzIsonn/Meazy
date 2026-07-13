package me.itzisonn_.meazy.lexer

enum class TokenBehavior {
    DEFAULT,

    /**
     * Token will be ignored during parsing if it doesn't match requested [TokenType]
     */
    IGNORE,

    /**
     * Token will be skipped during tokenization and not added to the final list
     */
    SKIP
}