package me.itzisonn_.meazy.lexer

object TokenTypeSets {
    private val _tokenTypeSets = mutableSetOf<TokenTypeSet>()

    fun add(tokenTypeSet: TokenTypeSet) { _tokenTypeSets += tokenTypeSet }
    fun get(id: String) = _tokenTypeSets.find { it.id == id }
    fun getAll() = _tokenTypeSets.toSet()

    val keywords get() = getNonNull("keywords")
    val operatorAssign get() = getNonNull("operator_assign")
    val operatorPostfix get() = getNonNull("operator_postfix")
    val memberAccess get() = getNonNull("member_access")
    val comparison get() = getNonNull("comparison")
    val multiplication get() = getNonNull("multiplication")
    val addition get() = getNonNull("addition")

    private fun getNonNull(id: String): TokenTypeSet {
        return get(id)!!
    }
}
