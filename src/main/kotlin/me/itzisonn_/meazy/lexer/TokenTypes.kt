package me.itzisonn_.meazy.lexer

/**
 * TokenTypes registrar
 */
object TokenTypes {
    private val tokenTypes = mutableSetOf<TokenType>()
    private var hasInitialized = false

    fun add(tokenType: TokenType) {
        require(get(tokenType.id) == null) { "TokenType with id '${tokenType.id}' already exists" }
        tokenTypes += tokenType
    }
    fun get(id: String) = tokenTypes.find { it.id == id }
    fun getAll() = tokenTypes.toList()

    internal fun initialize() {
        check(!hasInitialized) { "TokenTypes have already been initialized" }
        hasInitialized = true

        add(TokenType("new_line", "\n*"))
        add(TokenType("white_space", "(?!\n)\\s", true))
        add(TokenType("end_of_file", null as? Regex))

        add(TokenType("comment", "\\/\\/[^\n]*", true))
        add(TokenType("multi_line_comment", "\\/\\*(?:(?!\\*\\/).)*\\*\\/", true))
        
        add(TokenType("import", "import"))
        add(TokenType("variable", "var|val"))
        add(TokenType("function", "fun"))
        add(TokenType("class", "class"))
        add(TokenType("interface", "interface"))
        add(TokenType("constructor", "constructor"))
        add(TokenType("base", "base"))
        add(TokenType("if", "if"))
        add(TokenType("else", "else"))
        add(TokenType("for", "for"))
        add(TokenType("in", "in"))
        add(TokenType("while", "while"))
        add(TokenType("return", "return"))
        add(TokenType("continue", "continue"))
        add(TokenType("break", "break"))
        add(TokenType("is", "islike|is"))

        add(TokenType("left_parenthesis", "\\("))
        add(TokenType("right_parenthesis", "\\)"))
        add(TokenType("left_brace", "\\{"))
        add(TokenType("right_brace", "\\}"))
        add(TokenType("left_bracket", "\\["))
        add(TokenType("right_bracket", "\\]"))
        add(TokenType("colon", ":"))
        add(TokenType("comma", ","))
        add(TokenType("dot", "\\."))
        add(TokenType("question", "\\?"))
        add(TokenType("question_dot", "\\?\\."))
        add(TokenType("question_colon", "\\?:"))
        add(TokenType("arrow", "->"))

        add(TokenType("assign", "="))
        add(TokenType("plus", "\\+"))
        add(TokenType("minus", "-"))
        add(TokenType("multiply", "\\*"))
        add(TokenType("divide", "\\/"))
        add(TokenType("percent", "%"))
        add(TokenType("power", "\\^"))
        add(TokenType("plus_assign", "\\+="))
        add(TokenType("minus_assign", "-="))
        add(TokenType("multiply_assign", "\\*="))
        add(TokenType("divide_assign", "\\/="))
        add(TokenType("percent_assign", "%="))
        add(TokenType("power_assign", "\\^="))
        add(TokenType("double_plus", "\\+\\+"))
        add(TokenType("double_minus", "--"))

        add(TokenType("and", "&&"))
        add(TokenType("or", "\\|\\|"))
        add(TokenType("inversion", "!"))
        add(TokenType("equals", "=="))
        add(TokenType("not_equals", "!="))
        add(TokenType("greater", ">"))
        add(TokenType("greater_or_equals", ">="))
        add(TokenType("less", "<"))
        add(TokenType("less_or_equals", "<="))
        
        add(TokenType("null", "null"))
        add(TokenType("number", "(0|([1-9][0-9]*))(\\.[0-9]+)?"))
        add(TokenType("string", "\"[^\"\n]*(\")?"))
        add(TokenType("boolean", "true|false"))
        add(TokenType("this", "this"))

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
            }
        )
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
