package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.parser.modifier.Modifier

interface ModifieredEnvironment : Environment {
    /**
     * @return This class environment's modifiers
     */
    val modifiers: Set<Modifier>

    fun hasModifier(modifier: Modifier) = modifiers.contains(modifier)
}
