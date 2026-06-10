package me.itzisonn_.meazy.runtime.environment

import kotlin.uuid.Uuid

/**
 * Represents environment for loops
 */
interface LoopEnvironment : LocalVariableDeclarationEnvironment {
    override fun getStartLabel(): Uuid
    override fun getEndLabel(): Uuid
}



class LoopEnvironmentImpl(parent: Environment, startLabel: Uuid, endLabel: Uuid) :
    LocalVariableDeclarationEnvironmentImpl(parent, startLabel, endLabel), LoopEnvironment {
    override fun getStartLabel(): Uuid {
        return super.getStartLabel() ?: error("StartLabel is null")
    }

    override fun getEndLabel(): Uuid {
        return super.getEndLabel() ?: error("EndLabel is null")
    }
}