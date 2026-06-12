package me.itzisonn_.meazy.lexer

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.registry.Registries

/**
 * TokenTypes registrar
 *
 * @see Registries.TOKEN_TYPES
 */
object TokenTypes {
    private var hasRegistered = false

    /**
     * Initializes [Registries.PARSING_FUNCTIONS] registry
     *
     *
     * *Don't use this method because it's called once at [Registries] initialization*
     *
     * @throws IllegalStateException If [Registries.PARSING_FUNCTIONS] registry has already been initialized
     */
    fun register() {
        check(!hasRegistered) { "ParsingFunctions have already been initialized" }
        hasRegistered = true

        Registries.TOKEN_TYPES.register(
            getDefaultIdentifier("id"), TokenType(
                "id",
                "[a-zA-Z_][a-zA-Z0-9_]*",
                false
            ) { string: String ->
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





    @JvmStatic
    fun NEW_LINE(): TokenType {
        return get("new_line")
    }

    @JvmStatic
    fun END_OF_FILE(): TokenType {
        return get("end_of_file")
    }


    @JvmStatic
    fun IMPORT(): TokenType {
        return get("import")
    }

    @JvmStatic
    fun VARIABLE(): TokenType {
        return get("variable")
    }
    
    @JvmStatic
    fun FUNCTION(): TokenType {
        return get("function")
    }
    
    @JvmStatic
    fun CLASS(): TokenType {
        return get("class")
    }
    
    @JvmStatic
    fun INTERFACE(): TokenType {
        return get("interface")
    }
    
    @JvmStatic
    fun CONSTRUCTOR(): TokenType {
        return get("constructor")
    }
    
    @JvmStatic
    fun BASE(): TokenType {
        return get("base")
    }
    
    @JvmStatic
    fun IF(): TokenType {
        return get("if")
    }
    
    @JvmStatic
    fun ELSE(): TokenType {
        return get("else")
    }
    
    @JvmStatic
    fun FOR(): TokenType {
        return get("for")
    }
    
    @JvmStatic
    fun IN(): TokenType {
        return get("in")
    }
    
    @JvmStatic
    fun WHILE(): TokenType {
        return get("while")
    }
    
    @JvmStatic
    fun RETURN(): TokenType {
        return get("return")
    }
    
    @JvmStatic
    fun CONTINUE(): TokenType {
        return get("continue")
    }
    
    @JvmStatic
    fun BREAK(): TokenType {
        return get("break")
    }
    
    @JvmStatic
    fun IS(): TokenType {
        return get("is")
    }
    

    @JvmStatic
    fun LEFT_PARENTHESIS(): TokenType {
        return get("left_parenthesis")
    }
    
    @JvmStatic
    fun RIGHT_PARENTHESIS(): TokenType {
        return get("right_parenthesis")
    }
    
    @JvmStatic
    fun LEFT_BRACE(): TokenType {
        return get("left_brace")
    }
    
    @JvmStatic
    fun RIGHT_BRACE(): TokenType {
        return get("right_brace")
    }
    
    @JvmStatic
    fun LEFT_BRACKET(): TokenType {
        return get("left_bracket")
    }
    
    @JvmStatic
    fun RIGHT_BRACKET(): TokenType {
        return get("right_bracket")
    }
    
    @JvmStatic
    fun COLON(): TokenType {
        return get("colon")
    }
    
    @JvmStatic
    fun COMMA(): TokenType {
        return get("comma")
    }
    
    @JvmStatic
    fun DOT(): TokenType {
        return get("dot")
    }
    
    @JvmStatic
    fun QUESTION(): TokenType {
        return get("question")
    }
    
    @JvmStatic
    fun QUESTION_DOT(): TokenType {
        return get("question_dot")
    }
    
    @JvmStatic
    fun QUESTION_COLON(): TokenType {
        return get("question_colon")
    }

    @JvmStatic
    fun ARROW(): TokenType {
        return get("arrow")
    }
    

    @JvmStatic
    fun ASSIGN(): TokenType {
        return get("assign")
    }
    
    @JvmStatic
    fun MINUS(): TokenType {
        return get("minus")
    }
    
    @JvmStatic
    fun POWER(): TokenType {
        return get("power")
    }
    

    @JvmStatic
    fun AND(): TokenType {
        return get("and")
    }
    
    @JvmStatic
    fun OR(): TokenType {
        return get("or")
    }
    
    @JvmStatic
    fun INVERSION(): TokenType {
        return get("inversion")
    }
    

    @JvmStatic
    fun NULL(): TokenType {
        return get("null")
    }
    
    @JvmStatic
    fun NUMBER(): TokenType {
        return get("number")
    }
    
    @JvmStatic
    fun STRING(): TokenType {
        return get("string")
    }
    
    @JvmStatic
    fun BOOLEAN(): TokenType {
        return get("boolean")
    }
    
    @JvmStatic
    fun THIS(): TokenType {
        return get("this")
    }
    
    @JvmStatic
    fun ID(): TokenType {
        return get("id")
    }
    
    private fun get(id: String): TokenType {
        return Registries.TOKEN_TYPES.getEntry(getDefaultIdentifier(id))?.value!!
    }
}
