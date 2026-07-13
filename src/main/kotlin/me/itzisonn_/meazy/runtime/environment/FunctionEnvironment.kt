package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.Parameter
import me.itzisonn_.meazy.parser.modifier.Modifier
import me.itzisonn_.meazy.runtime.environment.declaration.FunctionDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.LocalVariableDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.LocalVariableDeclarationEnvironmentImpl
import kotlin.uuid.Uuid

/**
 * Represents environment for functions
 */
sealed interface FunctionEnvironment : LocalVariableDeclarationEnvironment, ModifieredEnvironment {
    /**
     * @return Id
     */
    val id: String

    /**
     * @return Parameters
     */
    val parameters: List<Parameter>

    var returnDataType: DataType?

    override fun getParent(): FunctionDeclarationEnvironment
}



private class FunctionEnvironmentImpl(
    parent: FunctionDeclarationEnvironment,
    startLabel: Uuid?,
    endLabel: Uuid?,
    override val id: String,
    parameters: List<Parameter>,
    override var returnDataType: DataType?,
    override val isShared: Boolean,
    modifiers: Set<Modifier>
) : LocalVariableDeclarationEnvironmentImpl(parent, startLabel, endLabel), FunctionEnvironment {
    override fun getParent() = super.getParent() as FunctionDeclarationEnvironment

    override val modifiers = modifiers.toSet()
        get() = field.toSet()

    override val parameters = parameters.toList()
        get() = field.toList()
}



/** TODO Javadoc
 * Creates function environment
 *
 * @param parent Parent
 * @param isShared Whether function environment is shared
 * @return New function environment
 */
fun FunctionEnvironment(
    parent: FunctionDeclarationEnvironment, startLabel: Uuid?, endLabel: Uuid?, id: String,
    parameters: List<Parameter>, returnDataType: DataType?,
    isShared: Boolean, modifiers: Set<Modifier>
): FunctionEnvironment = FunctionEnvironmentImpl(
    parent, startLabel, endLabel, id, parameters, returnDataType, isShared, modifiers
)