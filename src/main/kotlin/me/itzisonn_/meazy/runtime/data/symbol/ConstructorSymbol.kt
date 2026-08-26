package me.itzisonn_.meazy.runtime.data.symbol

import me.itzisonn_.meazy.runtime.data.Parameter
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment

/**
 * Represents constructor symbol
 */
sealed interface ConstructorSymbol : Symbol {
    val parameters: List<Parameter>
    val environment: ConstructorEnvironment



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



private data class ConstructorSymbolImpl(
    override val parameters: List<Parameter>,
    override val modifiers: Set<Modifier>,
    override val environment: ConstructorEnvironment
) : ConstructorSymbol

fun ConstructorSymbol(
    parameters: List<Parameter>, modifiers: Set<Modifier>, environment: ConstructorEnvironment
): ConstructorSymbol = ConstructorSymbolImpl(
    parameters.toList(), modifiers.toSet(), environment
)