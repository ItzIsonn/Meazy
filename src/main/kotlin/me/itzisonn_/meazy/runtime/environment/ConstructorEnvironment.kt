package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.runtime.data.Parameter
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.environment.declaration.ConstructorDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.variable.LocalVariableDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.variable.LocalVariableDeclarationEnvironmentImpl
import kotlin.uuid.Uuid

/**
 * Represents environment for constructors
 */
sealed interface ConstructorEnvironment : LocalVariableDeclarationEnvironment, ModifieredEnvironment {
    override fun getParent(): ConstructorDeclarationEnvironment

    val parameters: List<Parameter>
}



private class ConstructorEnvironmentImpl(
    parent: ConstructorDeclarationEnvironment,
    startLabel: Uuid?,
    endLabel: Uuid?,
    modifiers: Set<Modifier>,
    parameters: List<Parameter>
) : ConstructorEnvironment,
    LocalVariableDeclarationEnvironmentImpl(parent, startLabel, endLabel) {
    override val isShared = false
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
    modifiers: Set<Modifier>, parameters: List<Parameter>
): ConstructorEnvironment = ConstructorEnvironmentImpl(
    parent, startLabel, endLabel, modifiers, parameters
)