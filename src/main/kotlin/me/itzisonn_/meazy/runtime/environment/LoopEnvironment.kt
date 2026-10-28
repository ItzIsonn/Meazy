package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.runtime.environment.declaration.variable.LocalVariableDeclarationEnvironment
import me.itzisonn_.meazy.runtime.environment.declaration.variable.LocalVariableDeclarationEnvironmentImpl
import kotlin.uuid.Uuid

/**
 * Represents environment for loops
 */
sealed interface LoopEnvironment : LocalVariableDeclarationEnvironment {
    fun getId(): String?
    override fun getStartLabel(): Uuid
    override fun getEndLabel(): Uuid
}



private class LoopEnvironmentImpl(
    parent: Environment,
    private val id: String?,
    startLabel: Uuid,
    endLabel: Uuid
) : LoopEnvironment,
    LocalVariableDeclarationEnvironmentImpl(parent, startLabel, endLabel) {
    override fun getId() = id
    override fun getStartLabel() = super.getStartLabel() ?: error("StartLabel is null")
    override fun getEndLabel() = super.getEndLabel() ?: error("EndLabel is null")
}



/** TODO javadoc
 * Creates non-shared loop environment
 *
 * @param parent Parent
 * @return New loop environment
 */
fun LoopEnvironment(parent: Environment, id: String?, startLabel: Uuid, endLabel: Uuid): LoopEnvironment =
    LoopEnvironmentImpl(parent, id, startLabel, endLabel)