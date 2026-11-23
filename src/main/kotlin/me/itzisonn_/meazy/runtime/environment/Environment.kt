package me.itzisonn_.meazy.runtime.environment

/**
 * Represents an environment
 */
interface Environment {
    /**
     * @return This environment's parent
     */
    fun getParent(): Environment?
}



abstract class EnvironmentImpl(private val parent: Environment) : Environment {
    override fun getParent(): Environment = parent
}