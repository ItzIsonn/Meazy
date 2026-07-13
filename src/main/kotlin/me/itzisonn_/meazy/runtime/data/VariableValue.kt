package me.itzisonn_.meazy.runtime.data

import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.environment.declaration.VariableDeclarationEnvironment

/**
 * Represents runtime variable value
 */
interface VariableValue {
    /**
     * @return Id
     */
    val id: String?

    /**
     * @return DataType
     */
    val dataType: DataType

    /**
     * @return Whether value is constant
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
     * @return Whether this variable value has given modifier
     */
    fun hasModifier(modifier: Modifier) = modifier in modifiers

    /**
     * @param id Modifier's id
     * @return Whether this variable value has modifier with given id
     */
    fun hasModifier(id: String) = modifiers.find { it.id == id } != null

    /**
     * @return Modifiers
     */
    val modifiers: Set<Modifier>
}



private data class VariableValueImpl(
    override val id: String?,
    override val dataType: DataType,
    override val isConstant: Boolean,
    override val modifiers: Set<Modifier>,
    override val slot: Int,
    override val initializer: Expression?,
    override val parentEnvironment: VariableDeclarationEnvironment
) : VariableValue

fun VariableValue(
    id: String?, dataType: DataType, isConstant: Boolean, modifiers: Set<Modifier>,
    slot: Int, initializer: Expression?, parentEnvironment: VariableDeclarationEnvironment
): VariableValue = VariableValueImpl(
    id, dataType, isConstant, modifiers.toSet(),
    slot, initializer, parentEnvironment
)