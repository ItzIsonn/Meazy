package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.runtime.environment.declaration.ConstructorDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.variable.LocalVariableDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.variable.LocalVariableDeclarationEnvironmentImpl
import kotlin.uuid.Uuid

/**
 * Represents environment for constructors
 */
sealed interface ConstructorEnvironment : LocalVariableDeclarationEnvironment {
    override fun getParent(): ConstructorDeclarationEnvironment
}



private class ConstructorEnvironmentImpl(
    parent: ConstructorDeclarationEnvironment,
    startLabel: Uuid?,
    endLabel: Uuid?
) : ConstructorEnvironment,
    LocalVariableDeclarationEnvironmentImpl(parent, startLabel, endLabel) {
    override val isShared = false
    override fun getParent() = super.getParent() as ConstructorDeclarationEnvironment
}



/** TODO javadoc
 * Creates constructor environment
 *
 * @param parent Parent
 * @return New constructor environment
 */
fun ConstructorEnvironment(parent: ConstructorDeclarationEnvironment, startLabel: Uuid?, endLabel: Uuid?): ConstructorEnvironment =
    ConstructorEnvironmentImpl(parent, startLabel, endLabel)