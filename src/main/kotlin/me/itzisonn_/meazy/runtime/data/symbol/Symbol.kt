package me.itzisonn_.meazy.runtime.data.symbol

import me.itzisonn_.meazy.runtime.data.modifier.Modifier

interface Symbol

interface SymbolWithModifiers : Symbol {
    /**
     * @param modifier Target modifier
     * @return Whether this variable has given modifier
     */
    fun hasModifier(modifier: Modifier) = modifier in modifiers

    /**
     * @param id Modifier's id
     * @return Whether this variable has modifier with given id
     */
    fun hasModifier(id: String) = modifiers.find { it.id == id } != null

    val modifiers: Set<Modifier>
}