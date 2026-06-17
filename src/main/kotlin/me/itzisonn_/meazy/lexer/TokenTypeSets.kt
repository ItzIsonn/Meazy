package me.itzisonn_.meazy.lexer

object TokenTypeSets {
    private val tokenTypeSets = mutableSetOf<TokenTypeSet>()

    fun add(tokenTypeSet: TokenTypeSet) {
        require(get(tokenTypeSet.id) == null) { "TokenTypeSet with id '${tokenTypeSet.id}' already exists" }
        tokenTypeSets += tokenTypeSet
    }
    fun get(id: String) = tokenTypeSets.find { it.id == id }
    fun getAll() = tokenTypeSets.toSet()

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
