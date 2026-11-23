package me.itzisonn_.meazy.runtime.data.symbol

import me.itzisonn_.meazy.runtime.data.Parameter
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment

/**
 * Represents constructor symbol
 */
sealed interface ConstructorSymbol : SymbolWithModifiers {
    val parameters: List<Parameter>
    val environment: ConstructorEnvironment
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