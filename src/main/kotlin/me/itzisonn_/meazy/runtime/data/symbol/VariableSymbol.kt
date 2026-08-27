package me.itzisonn_.meazy.runtime.data.symbol

import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.environment.declaration.variable.GlobalVariableDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.variable.LocalVariableDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.variable.VariableDeclarationEnvironment

/**
 * Represents runtime variable symbol
 */
sealed interface VariableSymbol : Symbol {
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

    /**
     * @return Parent environment
     */
    val parentEnvironment: VariableDeclarationEnvironment<*>



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

sealed interface LocalVariableSymbol : VariableSymbol {
    //TODO
    val slot: Int
}

sealed interface GlobalVariableSymbol : VariableSymbol {
    override val id: String
    val initializer: Expression?
}



private data class LocalVariableSymbolImpl(
    override val id: String?,
    override val dataType: DataType,
    override val isConstant: Boolean,
    override val modifiers: Set<Modifier>,
    override val slot: Int,
    override val parentEnvironment: LocalVariableDeclarationEnvironment
) : LocalVariableSymbol

fun LocalVariableSymbol(
    id: String?, dataType: DataType, isConstant: Boolean, modifiers: Set<Modifier>,
    slot: Int, parentEnvironment: LocalVariableDeclarationEnvironment
): LocalVariableSymbol = LocalVariableSymbolImpl(
    id, dataType, isConstant, modifiers.toSet(),
    slot, parentEnvironment
)



private data class GlobalVariableSymbolImpl(
    override val id: String,
    override val dataType: DataType,
    override val isConstant: Boolean,
    override val modifiers: Set<Modifier>,
    override val initializer: Expression?,
    override val parentEnvironment: GlobalVariableDeclarationEnvironment
) : GlobalVariableSymbol

fun GlobalVariableSymbol(
    id: String, dataType: DataType, isConstant: Boolean, modifiers: Set<Modifier>,
    initializer: Expression?, parentEnvironment: GlobalVariableDeclarationEnvironment
): GlobalVariableSymbol = GlobalVariableSymbolImpl(
    id, dataType, isConstant, modifiers.toSet(),
    initializer, parentEnvironment
)