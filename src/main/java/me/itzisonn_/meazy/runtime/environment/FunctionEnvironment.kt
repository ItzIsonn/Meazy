package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression
import me.itzisonn_.meazy.parser.modifier.Modifier
import kotlin.uuid.Uuid

/**
 * Represents environment for functions
 */
interface FunctionEnvironment : LocalVariableDeclarationEnvironment, ModifieredEnvironment {
    /**
     * @return Id
     */
    val id: String

    /**
     * @return Parameters
     */
    val parameters: List<ParameterExpression>

    var returnDataType: DataType?

    override fun getParent(): FunctionDeclarationEnvironment
}



class FunctionEnvironmentImpl(
    parent: FunctionDeclarationEnvironment,
    startLabel: Uuid?,
    endLabel: Uuid?,
    override val id: String,
    parameters: List<ParameterExpression>,
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