package me.itzisonn_.meazy.runtime.data.symbol

import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.data.Parameter
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment

/**
 * Represents function symbol
 */
sealed interface FunctionSymbol : Symbol {
    val id: String
    val parameters: List<Parameter>
    val returnDataType: DataType?
    val environment: FunctionEnvironment



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



private data class FunctionSymbolImpl(
    override val id: String,
    override val parameters: List<Parameter>,
    override val returnDataType: DataType?,
    override val modifiers: Set<Modifier>,
    override val environment: FunctionEnvironment
) : FunctionSymbol

fun FunctionSymbol(
    id: String, parameters: List<Parameter>, returnDataType: DataType?,
    modifiers: Set<Modifier>, environment: FunctionEnvironment
): FunctionSymbol = FunctionSymbolImpl(
    id, parameters.toList(), returnDataType, modifiers.toSet(), environment
)