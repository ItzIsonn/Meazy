package me.itzisonn_.meazy.lexer

import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.meazy.registry.defaultIdentifier

/**
 * TokenTypes registrar
 *
 * @see Registries.TOKEN_TYPES
 */
object TokenTypes {
    private var hasRegistered = false

    /**
     * Initializes [Registries.TOKEN_TYPES] registry
     *
     * *Don't use this method because it's called once at [Registries] initialization*
     *
     * @throws IllegalStateException If [Registries.TOKEN_TYPES] registry has already been initialized
     */
    fun register() {
        check(!hasRegistered) { "ParsingFunctions have already been initialized" }
        hasRegistered = true

        Registries.TOKEN_TYPES.register(
            defaultIdentifier("id"), TokenType(
                "id",
                "[a-zA-Z_][a-zA-Z0-9_]*",
                false
            ) { string ->
                for (tokenType in TokenTypeSets.keywords.getTokenTypes()) {
                    if (tokenType.pattern?.matcher(string)?.matches() == true) {
                        return@TokenType false
                    }
                }
                true
            })
    }



    val newLine get() = get("new_line")
    val endOfFile get() = get("end_of_file")

    val import get() = get("import")
    val variable get() = get("variable")
    val function get() = get("function")
    val `class` get() = get("class")
    val `interface` get() = get("interface")
    val `constructor` get() = get("constructor")
    val base get() = get("base")
    val `if` get() = get("if")
    val `else` get() = get("else")
    val `for` get() = get("for")
    val `in` get() = get("in")
    val `while` get() = get("while")

    val `return` get() = get("return")
    val `continue` get() = get("continue")
    val `break` get() = get("break")
    val `is` get() = get("is")

    val leftParenthesis get() = get("left_parenthesis")
    val rightParenthesis get() = get("right_parenthesis")
    val leftBrace get() = get("left_brace")
    val rightBrace get() = get("right_brace")
    val leftBracket get() = get("left_bracket")
    val rightBracket get() = get("right_bracket")
    val colon get() = get("colon")
    val comma get() = get("comma")
    val dot get() = get("dot")
    val question get() = get("question")
    val questionDot get() = get("question_dot")
    val questionColon get() = get("question_colon")
    val arrow get() = get("arrow")
    
    val assign get() = get("assign")
    val minus get() = get("minus")
    val power get() = get("power")
    
    val and get() = get("and")
    val or get() = get("or")
    val inversion get() = get("inversion")
    
    val `null` get() = get("null")
    val number get() = get("number")
    val string get() = get("string")
    val boolean get() = get("boolean")
    val `this` get() = get("this")
    val id get() = get("id")
    
    private fun get(id: String): TokenType {
        return Registries.TOKEN_TYPES.getEntry(defaultIdentifier(id))?.value!!
    }
}
