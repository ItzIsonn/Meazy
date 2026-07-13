package me.itzisonn_.meazy.parser

import me.itzisonn_.meazy.lexer.Token
import me.itzisonn_.meazy.lexer.TokenBehaviour
import me.itzisonn_.meazy.lexer.TokenType
import me.itzisonn_.meazy.lexer.TokenTypeSet
import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.util.text.Text

/**
 * Is used to store and parse tokens
 *
 * @param tokens  List of tokens
 */
class Parser(tokens: List<Token>) {
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
    private val current get() = tokens[pos]

    /**
     * @return Token at position [i]
     */
    operator fun get(i: Int) = tokens[i]

    fun isEndOfFile() = current.type == TokenTypes.endOfFile



    /**
     * Returns token at current position and increments position by 1
     * @return Token at [pos] in tokens
     */
    fun consume() = current.also { pos++ }

    /**
     * Returns token at current position increments position by 1
     * 
     * @param tokenType Required TokenType
     * @param text      Exception's text
     * @return Token at [pos] in tokens
     *
     * @throws UnexpectedTokenException If token's type doesn't match required
     */
    fun consume(tokenType: TokenType, text: Text?): Token {
        while (current.type != tokenType) {
            if (current.type.behaviour == TokenBehaviour.IGNORE) pos++
            else throw UnexpectedTokenException(text)
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
    fun consume(tokenTypeSet: TokenTypeSet, text: Text?): Token {
        while (current.type !in tokenTypeSet.getTokenTypes()) {
            if (current.type.behaviour == TokenBehaviour.IGNORE) pos++
            else throw UnexpectedTokenException(text)
        }

        return consume()
    }

    fun isNext(tokenType: TokenType): Boolean {
        val prevPos = pos

        while (current.type != tokenType) {
            if (current.type.behaviour == TokenBehaviour.IGNORE) pos++
            else {
                pos = prevPos
                return false
            }
        }

        pos = prevPos
        return true
    }

    fun isNext(tokenTypeSet: TokenTypeSet): Boolean {
        val prevPos = pos

        while (current.type !in tokenTypeSet) {
            if (current.type.behaviour == TokenBehaviour.IGNORE) pos++
            else {
                pos = prevPos
                return false
            }
        }

        pos = prevPos
        return true
    }

    /** TODO
     * Returns token at current position increments position by 1
     *
     * @param tokenType Required TokenType
     * @param text      Exception's text
     * @return Token at [pos] in tokens
     *
     * @throws UnexpectedTokenException If token's type doesn't match required
     */
    fun find(tokenType: TokenType, text: Text?): Token {
        while (current.type != tokenType) {
            if (current.type.behaviour == TokenBehaviour.IGNORE) pos++
            else throw UnexpectedTokenException(text)
        }

        return current
    }

    fun UnexpectedTokenException(text: Text?) = UnexpectedTokenException(current, text)
    fun InvalidStatementException(text: Text) = InvalidStatementException(current.line, text)
    fun InvalidSyntaxException(text: Text) = InvalidSyntaxException(current.line, text)



    /**
     * Executes given ParsingFunction
     *
     * @param parsingFunction ParsingFunction to execute
     * @param extra           Extra info
     * @param T               Returned program unit's type
     * @return Parsed program unit
     */
    fun <T : ProgramUnit> parse(parsingFunction: ParsingFunction<T>, vararg extra: Any?): T {
        return parsingFunction.run { this@Parser.parse(*extra) }
    }

    /**
     * Executes given ParsingFunction, and if it fails, returns to position before parsing
     *
     * @param parsingFunction ParsingFunction to execute
     * @param extra           Extra info
     * @param T               Returned program unit's type
     * @return Parsed program unit
     */
    fun <T : ProgramUnit> tryParse(parsingFunction: ParsingFunction<T>, vararg extra: Any?): T? {
        val prevPos = pos

        try {
            return parse(parsingFunction, *extra)
        }
        catch (_: UnexpectedTokenException) {
            pos = prevPos
            return null
        }
    }
}