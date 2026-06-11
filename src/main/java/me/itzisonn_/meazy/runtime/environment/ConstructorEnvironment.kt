package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression
import me.itzisonn_.meazy.parser.modifier.Modifier
import kotlin.uuid.Uuid

/**
 * Represents environment for constructors
 */
sealed interface ConstructorEnvironment : LocalVariableDeclarationEnvironment, ModifieredEnvironment {
    override fun getParent(): ConstructorDeclarationEnvironment

    val parameters: List<ParameterExpression>
}



private class ConstructorEnvironmentImpl(
    parent: ConstructorDeclarationEnvironment,
    startLabel: Uuid?,
    endLabel: Uuid?,
    modifiers: Set<Modifier>,
    parameters: List<ParameterExpression>
) : LocalVariableDeclarationEnvironmentImpl(parent, startLabel, endLabel), ConstructorEnvironment {
    override val isShared get() = false
    override fun getParent() = super.getParent() as ConstructorDeclarationEnvironment

    override val modifiers = modifiers.toSet()
        get() = field.toSet()

    override val parameters = parameters.toList()
        get() = field.toList()
}



/** TODO javadoc
 * Creates constructor environment
 *
 * @param parent Parent
 * @return New constructor environment
 */
fun ConstructorEnvironment(
    parent: ConstructorDeclarationEnvironment, startLabel: Uuid?, endLabel: Uuid?,
    modifiers: Set<Modifier>, parameters: List<ParameterExpression>
): ConstructorEnvironment = ConstructorEnvironmentImpl(
    parent, startLabel, endLabel, modifiers, parameters
)