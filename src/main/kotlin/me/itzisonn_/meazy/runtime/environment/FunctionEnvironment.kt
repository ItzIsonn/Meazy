package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.runtime.environment.declaration.FunctionDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.variable.LocalVariableDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.variable.LocalVariableDeclarationEnvironmentImpl
import kotlin.uuid.Uuid

/**
 * Represents environment for functions
 */
sealed interface FunctionEnvironment : LocalVariableDeclarationEnvironment {
    override fun getParent(): FunctionDeclarationEnvironment
}



private class FunctionEnvironmentImpl(
    parent: FunctionDeclarationEnvironment,
    startLabel: Uuid?,
    endLabel: Uuid?,
    override val isShared: Boolean
) : FunctionEnvironment,
    LocalVariableDeclarationEnvironmentImpl(parent, startLabel, endLabel) {
    override fun getParent() = super.getParent() as FunctionDeclarationEnvironment
}



/** TODO Javadoc
 * Creates function environment
 *
 * @param parent Parent
 * @param isShared Whether function environment is shared
 * @return New function environment
 */
fun FunctionEnvironment(parent: FunctionDeclarationEnvironment, startLabel: Uuid?, endLabel: Uuid?, isShared: Boolean): FunctionEnvironment =
    FunctionEnvironmentImpl(parent, startLabel, endLabel, isShared)