package me.itzisonn_.meazy.runtime.data.symbol

import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
import me.itzisonn_.meazy.runtime.environment.getClass
import me.itzisonn_.meazy.runtime.environment.resolveClassDesc
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs

/**
 * Represents class symbol
 */
sealed interface ClassSymbol : SymbolWithModifiers {
    val id: String
    val isInterface: Boolean
    val classDesc: ClassDesc
    val environment: ClassEnvironment

    /**
     * ClassSymbol of this class environment's base class
     */
    val baseClass: ClassSymbol?
    val interfaces: Set<ClassSymbol>
    fun resolveBaseClasses()
}



private class ClassSymbolImpl private constructor(
    override val id: String,
    override val isInterface: Boolean,
    override val modifiers: Set<Modifier>,
    override val environment: ClassEnvironment,
    baseClass: ClassSymbol?,
    interfaces: Set<ClassSymbol>,
    unresolvedBaseClasses: Set<String>
) : ClassSymbol {
    override var baseClass = baseClass
        private set
    private val _interfaces = interfaces.toMutableSet()
    private val unresolvedBaseClasses = unresolvedBaseClasses.toMutableSet()

    constructor(id: String, isInterface: Boolean, modifiers: Set<Modifier>, environment: ClassEnvironment, baseClass: ClassSymbol?, interfaces: Set<ClassSymbol>) :
            this(id, isInterface, modifiers.toSet(), environment, baseClass, interfaces, setOf())

    constructor(id: String, isInterface: Boolean, modifiers: Set<Modifier>, environment: ClassEnvironment, unresolvedBaseClasses: Set<String>) :
            this(id, isInterface, modifiers.toSet(), environment, null, setOf(), unresolvedBaseClasses)

    override val classDesc: ClassDesc get() = ClassDesc.of(environment.fullClassName)
    override val interfaces get() = _interfaces.toSet()

    override fun resolveBaseClasses() {
        for (unresolvedBaseClass in unresolvedBaseClasses) {
            val classDesc = environment.getParent().resolveClassDesc(unresolvedBaseClass, false)
            val baseClassSymbol = environment.getParent().getClass(classDesc) ?: error("Can't find base class $classDesc")

            if (baseClassSymbol.isInterface) _interfaces.add(baseClassSymbol)
            else {
                if (baseClass != null) throw RuntimeException("Class can't have more than one base class TODO")
                baseClass = baseClassSymbol
            }
        }

        if (baseClass == null && !isInterface) baseClass = environment.getClass(ConstantDescs.CD_Object)
        unresolvedBaseClasses.clear()
    }
}

fun ClassSymbol(id: String, isInterface: Boolean, modifiers: Set<Modifier>, environment: ClassEnvironment, baseClass: ClassSymbol?, interfaces: Set<ClassSymbol>): ClassSymbol =
    ClassSymbolImpl(id, isInterface, modifiers.toSet(), environment, baseClass, interfaces)

fun ClassSymbol(id: String, isInterface: Boolean, modifiers: Set<Modifier>, environment: ClassEnvironment, unresolvedBaseClasses: Set<String>): ClassSymbol =
    ClassSymbolImpl(id, isInterface, modifiers.toSet(), environment, unresolvedBaseClasses)