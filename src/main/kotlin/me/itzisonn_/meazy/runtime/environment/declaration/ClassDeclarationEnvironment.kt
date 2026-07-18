package me.itzisonn_.meazy.runtime.environment.declaration

import me.itzisonn_.meazy.runtime.EvaluationException
import me.itzisonn_.meazy.runtime.data.symbol.ClassSymbol
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.EnvironmentImpl
import me.itzisonn_.meazy.util.text.translatable

/**
 * Adds to Environment ability to declare classes
 */
interface ClassDeclarationEnvironment : Environment {
    /**
     * Declares given class in this environment
     * TODO
     */
    fun declareClass(cls: ClassSymbol)

    /**
     * @param id Class's id
     * @return Declared class with given id or null
     */
    fun getClass(id: String): ClassSymbol? {
        return classes.find { it.id == id }
    }

    /**
     * @return All declared classes
     */
    val classes: Set<ClassSymbol>
}



private class ClassDeclarationEnvironmentImpl(
    parent: Environment,
    override val isShared: Boolean
) : ClassDeclarationEnvironment, EnvironmentImpl(parent) {
    private val _classes = mutableSetOf<ClassSymbol>()

    override val classes get() = _classes.toSet()

    override fun declareClass(cls: ClassSymbol) {
        for (otherEnvironment in classes) {
            if (otherEnvironment.id == cls.id) {
                throw EvaluationException(translatable("runtime.class.already_exists", cls.id))
            }
        }

        _classes.add(cls)
    }
}



fun ClassDeclarationEnvironment(parent: Environment, isShared: Boolean): ClassDeclarationEnvironment =
    ClassDeclarationEnvironmentImpl(parent, isShared)