package me.itzisonn_.meazy.runtime.data.symbol

import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import java.lang.constant.ClassDesc

/**
 * Represents class symbol
 */
sealed interface ClassSymbol : Symbol {
    val id: String
    val isInterface: Boolean
    val unresolvedBaseClasses: Set<String>
    val classDesc: ClassDesc
    val environment: ClassEnvironment



    /**
     * @param modifier Target modifier
     * @return Whether this variable has given modifier
     */
    fun hasModifier(modifier: Modifier) = modifier in modifiers

    /**
     * @param id Modifier's id
     * @return Whether this variable has modifier with given id
     */
    fun hasModifier(id: String) = modifiers.find { it.id == id } != null

    val modifiers: Set<Modifier>
}



private data class ClassSymbolImpl(
    override val id: String,
    override val isInterface: Boolean,
    override val unresolvedBaseClasses: Set<String>,
    override val modifiers: Set<Modifier>,
    override val environment: ClassEnvironment
) : ClassSymbol {
    override val classDesc: ClassDesc get() = ClassDesc.of(environment.fullClassName)
}

fun ClassSymbol(
    id: String, isInterface: Boolean, unresolvedBaseClasses: Set<String>,
    modifiers: Set<Modifier>, environment: ClassEnvironment
): ClassSymbol = ClassSymbolImpl(
    id, isInterface, unresolvedBaseClasses.toSet(), modifiers.toSet(), environment
)