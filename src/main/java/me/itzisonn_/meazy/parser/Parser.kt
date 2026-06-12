package me.itzisonn_.meazy.parser

import me.itzisonn_.meazy.lexer.Token
import me.itzisonn_.meazy.lexer.TokenType
import me.itzisonn_.meazy.lexer.TokenTypeSet
import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.lexer.TokenTypes.NEW_LINE
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.meazy.text.Text
import me.itzisonn_.registry.RegistryIdentifier

/**
 * Is used to store and parse tokens
 *
 * @param context Parsing context
 * @param tokens  List of tokens
 * 
 * @see Registries.PARSING_FUNCTIONS
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
    fun getCurrentAndNext(): Token {
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
    fun getCurrentAndNext(tokenType: TokenType, text: Text): Token {
        if (current.type != tokenType) {
            throw UnexpectedTokenException(current.line, text)
        }

        return getCurrentAndNext()
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
    fun getCurrentAndNext(tokenTypeSet: TokenTypeSet, text: Text): Token {
        if (current.type !in tokenTypeSet.getTokenTypes()) {
            throw UnexpectedTokenException(current.line, text)
        }

        return getCurrentAndNext()
    }

    /**
     * Skips all [TokenTypes.newLine] tokens
     */
    fun moveOverOptionalNewLines() {
        while (current.type == TokenTypes.newLine) pos++
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
            if (current == TokenTypes.newLine) return false
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
            if (current == NEW_LINE()) return false
            if (current in tokenTypeSet) return true
        }

        return false
    }



    /**
     * Executes ParsingFunction with given id
     * 
     * @param id    Id of ParsingFunction
     * @param extra Extra info
     * @return Parsed program unit
     *
     * @throws IllegalArgumentException When can't find ParsingFunction with given id
     */
    fun parse(id: RegistryIdentifier, vararg extra: Any?): ProgramUnit {
        val parsingFunction = getParsingFunctionOrNull(id)
        requireNotNull(parsingFunction) { "Can't find ParsingFunction with id $id" }
        return parsingFunction.parse(context, *extra)
    }

    /**
     * Executes ParsingFunction with given id
     * 
     * @param id    Id of ParsingFunction
     * @param cls   Required program unit's class
     * @param extra Extra info
     * @param T     Returned program unit's type
     * @return Parsed program unit
     *
     * @throws IllegalArgumentException When can't find ParsingFunction with given id
     * or return type of ParsingFunction doesn't match requested
     */
    fun <T : ProgramUnit> parse(id: RegistryIdentifier, cls: Class<T>, vararg extra: Any?): T {
        val programUnit = parse(id, *extra)

        require(cls.isInstance(programUnit)) {
            "Return type of ParsingFunction with id $id doesn't match requested (${cls.getName()})"
        }

        return cls.cast(programUnit)
    }

    /**
     * Executes ParsingFunction after ParsingFunction with given id
     * 
     * @param id    Id of ParsingFunction
     * @param extra Extra info
     * @return Parsed program unit
     *
     * @throws IllegalArgumentException When can't find ParsingFunction with given id
     */
    fun parseAfter(id: RegistryIdentifier, vararg extra: Any?): ProgramUnit {
        val parsingFunction = getParsingFunctionAfterOrNull(id)
        requireNotNull(parsingFunction) { "Can't find ParsingFunction with id $id" }
        return parsingFunction.parse(context, *extra)
    }

    /**
     * Executes ParsingFunction after ParsingFunction with given id
     * 
     * @param id    Id of ParsingFunction
     * @param cls   Required program unit's class
     * @param extra Extra info
     * @param T     Returned program unit's type
     * @return Parsed program unit
     *
     * @throws IllegalArgumentException When can't find ParsingFunction with given id
     * or return type of ParsingFunction doesn't match requested
     */
    fun <T : ProgramUnit> parseAfter(id: RegistryIdentifier, cls: Class<T>, vararg extra: Any?): T {
        val programUnit = parseAfter(id, *extra)

        require(cls.isInstance(programUnit)) {
            "Return type of ParsingFunction with id $id doesn't match requested (${cls.getName()})"
        }

        return cls.cast(programUnit)
    }



    /**
     * Finds ParsingFunction with given id
     * 
     * @param id Id
     * @return ParsingFunction with given id or null
     */
    private fun getParsingFunctionOrNull(id: RegistryIdentifier): ParsingFunction<out ProgramUnit>? {
        val entry = Registries.PARSING_FUNCTIONS.getEntry(id) ?: return null
        return entry.getValue()
    }

    /**
     * Finds ParsingFunction after ParsingFunction with given id
     * 
     * @param id Id
     * @return ParsingFunction after ParsingFunction with given id or null
     */
    private fun getParsingFunctionAfterOrNull(id: RegistryIdentifier): ParsingFunction<out ProgramUnit>? {
        val entry = Registries.PARSING_FUNCTIONS.getEntryAfter(id) ?: return null
        return entry.getValue()
    }
}