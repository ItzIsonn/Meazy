package me.itzisonn_.meazy.parser

import me.itzisonn_.meazy.lexer.Token
import me.itzisonn_.meazy.lexer.TokenType
import me.itzisonn_.meazy.lexer.TokenTypeSet
import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.text.Text

/**
 * Is used to store and parse tokens
 *
 * @param context Parsing context
 * @param tokens  List of tokens
 */
class Parser(
    private val context: ParsingContext,
    tokens: List<Token>
) {
    private val tokens = tokens.toList()

    /**
     * Position of current element
     */
    var pos = 0
        private set

    /**
     * Amount of tokens
     */
    val size get() = tokens.size

    /**
     * @return Token at position [pos]
     */
    val current get() = tokens[pos]

    /**
     * @return Token at position [i]
     */
    operator fun get(i: Int) = tokens[i]



    /**
     * Increments position by 1
     */
    fun next() { pos++ }

    /**
     * Increments position by 1
     * 
     * @param tokenType Required TokenType
     * @param text      Exception's text
     *
     * @throws UnexpectedTokenException If token's type doesn't match required
     */
    fun next(tokenType: TokenType, text: Text) {
        if (current.type != tokenType) {
            throw UnexpectedTokenException(current.line, text)
        }

        next()
    }

    /**
     * Increments position by 1
     * 
     * @param tokenTypeSet Required TokenTypeSet
     * @param text         Exception's text
     *
     * @throws UnexpectedTokenException If tokenTypeSet doesn't contain current token's type
     */
    fun next(tokenTypeSet: TokenTypeSet, text: Text) {
        if (current.type !in tokenTypeSet.getTokenTypes()) {
            throw UnexpectedTokenException(current.line, text)
        }

        next()
    }



    /**
     * Returns token at current position and increments position by 1\
     * @return Token at [pos] in tokens
     */
    fun consume(): Token {
        val token = current
        pos++
        return token
    }

    /**
     * Returns token at current position increments position by 1
     * 
     * @param tokenType Required TokenType
     * @param text      Exception's text
     * @return Token at [pos] in tokens
     *
     * @throws UnexpectedTokenException If token's type doesn't match required
     */
    fun consume(tokenType: TokenType, text: Text): Token {
        if (current.type != tokenType) {
            throw UnexpectedTokenException(current.line, text)
        }

        return consume()
    }

    /**
     * Returns token at current position and increments position by 1
     * 
     * @param tokenTypeSet Required TokenTypeSet
     * @param text         Exception's text
     * @return Token at [pos] in tokens
     *
     * @throws UnexpectedTokenException If tokenTypeSet doesn't contain current token's type
     */
    fun consume(tokenTypeSet: TokenTypeSet, text: Text): Token {
        if (current.type !in tokenTypeSet.getTokenTypes()) {
            throw UnexpectedTokenException(current.line, text)
        }

        return consume()
    }

    /**
     * Skips all [TokenTypes.newLine] tokens
     */
    fun skipNewLines() {
        while (current.type == newLine) pos++
    }

    /**
     * Checks current line for presence of token with given tokenType
     * 
     * @param tokenType Required TokenType
     * @return Whether current line has token with given tokenType
     */
    fun currentLineHasToken(tokenType: TokenType): Boolean {
        for (i in pos..<tokens.size) {
            val current = tokens[i].type
            if (current == newLine) return false
            if (current == tokenType) return true
        }

        return false
    }

    /**
     * Checks current line for presence of token with type inside given tokenTypeSet
     * 
     * @param tokenTypeSet Required TokenTypeSet
     * @return Whether current line has token with type inside given tokenTypeSet
     */
    fun currentLineHasToken(tokenTypeSet: TokenTypeSet): Boolean {
        for (i in pos..<tokens.size) {
            val current = tokens[i].type
            if (current == newLine) return false
            if (current in tokenTypeSet) return true
        }

        return false
    }



    /**
     * Executes given ParsingFunction
     *
     * @param parsingFunction ParsingFunction to execute
     * @param extra           Extra info
     * @param T               Returned program unit's type
     * @return Parsed program unit
     */
    fun <T : ProgramUnit> parse(parsingFunction: ParsingFunction<T>, vararg extra: Any?): T {
        val programUnit = parsingFunction.parse(context, *extra)
        return programUnit
    }
}