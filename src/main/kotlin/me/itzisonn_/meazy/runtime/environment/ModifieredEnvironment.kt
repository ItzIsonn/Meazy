package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.runtime.data.modifier.Modifier

interface ModifieredEnvironment : Environment {
    /**
     * @return This class environment's modifiers
     */
    val modifiers: Set<Modifier>

    fun hasModifier(modifier: Modifier) = modifier in modifiers
}
