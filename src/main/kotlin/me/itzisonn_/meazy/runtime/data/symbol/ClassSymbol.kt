package me.itzisonn_.meazy.runtime.data.symbol

import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import java.lang.constant.ClassDesc

/**
 * Represents class symbol
 */
sealed interface ClassSymbol : SymbolWithModifiers {
    val id: String
    val isInterface: Boolean
    val classDesc: ClassDesc
    val environment: ClassEnvironment
}



private data class ClassSymbolImpl(
    override val id: String,
    override val isInterface: Boolean,
    override val modifiers: Set<Modifier>,
    override val environment: ClassEnvironment
) : ClassSymbol {
    override val classDesc: ClassDesc get() = ClassDesc.of(environment.fullClassName)
}

fun ClassSymbol(id: String, isInterface: Boolean, modifiers: Set<Modifier>, environment: ClassEnvironment): ClassSymbol =
    ClassSymbolImpl(id, isInterface, modifiers.toSet(), environment)