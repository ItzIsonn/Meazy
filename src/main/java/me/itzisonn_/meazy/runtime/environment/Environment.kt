package me.itzisonn_.meazy.runtime.environment

/**
 * Represents an environment
 */
interface Environment {
    /**
     * @return This environment's parent
     */
    fun getParent(): Environment?

    /**
     * @return Whether this environment is shared
     */
    val isShared: Boolean

    val fullClassName: String?
        get() = null
}