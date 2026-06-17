package me.itzisonn_.meazy.lexer

/**
 * Represents code unit
 *
 * @param line  Line on which this token is located
 * @param start Start index of this token
 * @param end   End index of this token
 * @param type  TokenType
 * @param value String that matches this token's type
 *
 * @throws IllegalArgumentException If line is negative
 */
class Token(
    val line: Int,
    val start: Int,
    val end: Int,
    val type: TokenType,
    val value: String
) {
    init {
        require(line >= 0) { "Line can't be negative" }
        require(start >= 0) { "Start can't be negative" }
        require(end >= 0) { "End can't be negative" }
    }

    override fun toString(): String {
        val inlinedValue = value.replace("\n", "\\\\n")
        return "Token($line,$type,$inlinedValue)"
    }
}