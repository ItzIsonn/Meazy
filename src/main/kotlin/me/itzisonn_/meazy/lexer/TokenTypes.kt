package me.itzisonn_.meazy.lexer

/**
 * TokenTypes registrar
 */
object TokenTypes {
    private val _tokenTypes = mutableSetOf<TokenType>()
    private var hasInitialized = false

    fun add(tokenType: TokenType) { _tokenTypes += tokenType }
    fun get(id: String) = _tokenTypes.find { it.id == id }
    fun getAll() = _tokenTypes.toList()

    internal fun initialize() {
        check(!hasInitialized) { "TokenTypes have already been initialized" }
        hasInitialized = true

        add(
            TokenType(
                "id",
                "[a-zA-Z_][a-zA-Z0-9_]*",
                false
            ) { string ->
                for (tokenType in TokenTypeSets.keywords.getTokenTypes()) {
                    if (tokenType.regex?.matches(string) == true) {
                        return@TokenType false
                    }
                }
                true
            })
    }



    val newLine get() = getNonNull("new_line")
    val endOfFile get() = getNonNull("end_of_file")

    val import get() = getNonNull("import")
    val variable get() = getNonNull("variable")
    val function get() = getNonNull("function")
    val `class` get() = getNonNull("class")
    val `interface` get() = getNonNull("interface")
    val `constructor` get() = getNonNull("constructor")
    val base get() = getNonNull("base")
    val `if` get() = getNonNull("if")
    val `else` get() = getNonNull("else")
    val `for` get() = getNonNull("for")
    val `in` get() = getNonNull("in")
    val `while` get() = getNonNull("while")

    val `return` get() = getNonNull("return")
    val `continue` get() = getNonNull("continue")
    val `break` get() = getNonNull("break")
    val `is` get() = getNonNull("is")

    val leftParenthesis get() = getNonNull("left_parenthesis")
    val rightParenthesis get() = getNonNull("right_parenthesis")
    val leftBrace get() = getNonNull("left_brace")
    val rightBrace get() = getNonNull("right_brace")
    val leftBracket get() = getNonNull("left_bracket")
    val rightBracket get() = getNonNull("right_bracket")
    val colon get() = getNonNull("colon")
    val comma get() = getNonNull("comma")
    val dot get() = getNonNull("dot")
    val question get() = getNonNull("question")
    val questionDot get() = getNonNull("question_dot")
    val questionColon get() = getNonNull("question_colon")
    val arrow get() = getNonNull("arrow")
    
    val assign get() = getNonNull("assign")
    val minus get() = getNonNull("minus")
    val power get() = getNonNull("power")
    
    val and get() = getNonNull("and")
    val or get() = getNonNull("or")
    val inversion get() = getNonNull("inversion")
    
    val `null` get() = getNonNull("null")
    val number get() = getNonNull("number")
    val string get() = getNonNull("string")
    val boolean get() = getNonNull("boolean")
    val `this` get() = getNonNull("this")
    val id get() = getNonNull("id")

    private fun getNonNull(id: String): TokenType {
        return get(id)!!
    }
}
