package me.itzisonn_.meazy.lexer

import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.meazy.registry.defaultIdentifier

object TokenTypeSets {
    val keywords get() = get("keywords")
    val operatorAssign get() = get("operator_assign")
    val operatorPostfix get() = get("operator_postfix")
    val memberAccess get() = get("member_access")
    val comparison get() = get("comparison")
    val multiplication get() = get("multiplication")
    val addition get() = get("addition")

    private fun get(id: String): TokenTypeSet {
        return Registries.TOKEN_TYPE_SETS.getEntry(defaultIdentifier(id))?.value!!
    }
}
