package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.runtime.environment.declaration.variable.LocalVariableDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.variable.LocalVariableDeclarationEnvironmentImpl
import kotlin.uuid.Uuid

/**
 * Represents environment for loops
 */
sealed interface LoopEnvironment : LocalVariableDeclarationEnvironment {
    override fun getStartLabel(): Uuid
    override fun getEndLabel(): Uuid
}



private class LoopEnvironmentImpl(
    parent: Environment,
    startLabel: Uuid,
    endLabel: Uuid
) : LoopEnvironment,
    LocalVariableDeclarationEnvironmentImpl(parent, startLabel, endLabel) {
    override fun getStartLabel(): Uuid {
        return super.getStartLabel() ?: error("StartLabel is null")
    }

    override fun getEndLabel(): Uuid {
        return super.getEndLabel() ?: error("EndLabel is null")
    }
}



/** TODO javadoc
 * Creates non-shared loop environment
 *
 * @param parent Parent
 * @return New loop environment
 */
fun LoopEnvironment(parent: Environment, startLabel: Uuid, endLabel: Uuid): LoopEnvironment =
    LoopEnvironmentImpl(parent, startLabel, endLabel)