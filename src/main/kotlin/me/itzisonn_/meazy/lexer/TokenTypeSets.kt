package me.itzisonn_.meazy.lexer

import me.itzisonn_.meazy.lexer.TokenTypes.`import`
import me.itzisonn_.meazy.lexer.TokenTypes.variable
import me.itzisonn_.meazy.lexer.TokenTypes.function
import me.itzisonn_.meazy.lexer.TokenTypes.`class`
import me.itzisonn_.meazy.lexer.TokenTypes.`interface`
import me.itzisonn_.meazy.lexer.TokenTypes.`constructor`
import me.itzisonn_.meazy.lexer.TokenTypes.base
import me.itzisonn_.meazy.lexer.TokenTypes.`if`
import me.itzisonn_.meazy.lexer.TokenTypes.`else`
import me.itzisonn_.meazy.lexer.TokenTypes.`for`
import me.itzisonn_.meazy.lexer.TokenTypes.`in`
import me.itzisonn_.meazy.lexer.TokenTypes.`while`
import me.itzisonn_.meazy.lexer.TokenTypes.`return`
import me.itzisonn_.meazy.lexer.TokenTypes.`continue`
import me.itzisonn_.meazy.lexer.TokenTypes.`break`
import me.itzisonn_.meazy.lexer.TokenTypes.`is`
import me.itzisonn_.meazy.lexer.TokenTypes.`null`
import me.itzisonn_.meazy.lexer.TokenTypes.boolean
import me.itzisonn_.meazy.lexer.TokenTypes.`this`
import me.itzisonn_.meazy.lexer.TokenTypes.plusAssign
import me.itzisonn_.meazy.lexer.TokenTypes.minusAssign
import me.itzisonn_.meazy.lexer.TokenTypes.multiplyAssign
import me.itzisonn_.meazy.lexer.TokenTypes.divideAssign
import me.itzisonn_.meazy.lexer.TokenTypes.percentAssign
import me.itzisonn_.meazy.lexer.TokenTypes.powerAssign
import me.itzisonn_.meazy.lexer.TokenTypes.doublePlus
import me.itzisonn_.meazy.lexer.TokenTypes.doubleMinus
import me.itzisonn_.meazy.lexer.TokenTypes.dot
import me.itzisonn_.meazy.lexer.TokenTypes.questionDot
import me.itzisonn_.meazy.lexer.TokenTypes.equals
import me.itzisonn_.meazy.lexer.TokenTypes.notEquals
import me.itzisonn_.meazy.lexer.TokenTypes.greater
import me.itzisonn_.meazy.lexer.TokenTypes.greaterOrEquals
import me.itzisonn_.meazy.lexer.TokenTypes.less
import me.itzisonn_.meazy.lexer.TokenTypes.lessOrEquals
import me.itzisonn_.meazy.lexer.TokenTypes.and
import me.itzisonn_.meazy.lexer.TokenTypes.or
import me.itzisonn_.meazy.lexer.TokenTypes.multiply
import me.itzisonn_.meazy.lexer.TokenTypes.divide
import me.itzisonn_.meazy.lexer.TokenTypes.percent
import me.itzisonn_.meazy.lexer.TokenTypes.plus
import me.itzisonn_.meazy.lexer.TokenTypes.minus

object TokenTypeSets {
    private val tokenTypeSets = mutableSetOf<TokenTypeSet>()
    private var hasInitialized = false

    fun add(tokenTypeSet: TokenTypeSet) {
        require(get(tokenTypeSet.id) == null) { "TokenTypeSet with id '${tokenTypeSet.id}' already exists" }
        tokenTypeSets += tokenTypeSet
    }
    fun get(id: String) = tokenTypeSets.find { it.id == id }
    fun getAll() = tokenTypeSets.toSet()

    internal fun initialize() {
        check(!hasInitialized) { "TokenTypes have already been initialized" }
        hasInitialized = true

        add(
            TokenTypeSet(
                "keywords",
                `import`, variable, function, `class`, `interface`,
                `constructor`, base, `if`, `else`, `for`, `in`, `while`,
                `return`, `continue`, `break`, `is`, `null`, boolean, `this`
            )
        )

        add(
            TokenTypeSet(
                "operator_assign",
                plusAssign, minusAssign, multiplyAssign,
                divideAssign, percentAssign, powerAssign
            )
        )

        add(TokenTypeSet("operator_postfix", doublePlus, doubleMinus))
        add(TokenTypeSet("member_access", dot, questionDot))

        add(
            TokenTypeSet(
                "comparison",
                equals, notEquals, greater, greaterOrEquals,
                less, lessOrEquals,
            )
        )

        add(TokenTypeSet("logical", and, or))
        add(TokenTypeSet("multiplication", multiply, divide, percent))
        add(TokenTypeSet("addition", plus, minus))
    }



    val keywords get() = getNonNull("keywords")
    val operatorAssign get() = getNonNull("operator_assign")
    val operatorPostfix get() = getNonNull("operator_postfix")
    val memberAccess get() = getNonNull("member_access")
    val comparison get() = getNonNull("comparison")
    val logical get() = getNonNull("logical")
    val multiplication get() = getNonNull("multiplication")
    val addition get() = getNonNull("addition")

    private fun getNonNull(id: String): TokenTypeSet {
        return get(id)!!
    }
}
