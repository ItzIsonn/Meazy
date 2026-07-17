package me.itzisonn_.meazy.runtime.data.symbol

import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.environment.declaration.VariableDeclarationEnvironment

/**
 * Represents runtime variable symbol
 */
sealed interface VariableSymbol {
    /**
     * @return Id
     */
    val id: String?

    /**
     * @return DataType
     */
    val dataType: DataType

    /**
     * @return Whether variable is constant
     */
    val isConstant: Boolean

    //TODO
    val slot: Int

    val initializer: Expression?

    /**
     * @return Parent environment
     */
    val parentEnvironment: VariableDeclarationEnvironment



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



private data class VariableSymbolImpl(
    override val id: String?,
    override val dataType: DataType,
    override val isConstant: Boolean,
    override val modifiers: Set<Modifier>,
    override val slot: Int,
    override val initializer: Expression?,
    override val parentEnvironment: VariableDeclarationEnvironment
) : VariableSymbol

fun VariableSymbol(
    id: String?, dataType: DataType, isConstant: Boolean, modifiers: Set<Modifier>,
    slot: Int, initializer: Expression?, parentEnvironment: VariableDeclarationEnvironment
): VariableSymbol = VariableSymbolImpl(
    id, dataType, isConstant, modifiers.toSet(),
    slot, initializer, parentEnvironment
)