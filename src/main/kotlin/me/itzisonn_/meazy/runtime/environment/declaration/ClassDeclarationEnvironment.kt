package me.itzisonn_.meazy.runtime.environment.declaration

import me.itzisonn_.meazy.runtime.EvaluationException
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment
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
    fun declareClass(classEnvironment: ClassEnvironment)

    /**
     * @param id Class's id
     * @return Declared class with given id or null
     */
    fun getClass(id: String): ClassEnvironment? {
        return classes.find { it.id == id }
    }

    /**
     * @return All declared classes
     */
    val classes: Set<ClassEnvironment>
}



private class ClassDeclarationEnvironmentImpl(
    parent: Environment,
    override val isShared: Boolean
) : ClassDeclarationEnvironment, EnvironmentImpl(parent) {
    private val _classes = mutableSetOf<ClassEnvironment>()

    override val classes get() = _classes.toSet()

    override fun declareClass(classEnvironment: ClassEnvironment) {
        for (otherEnvironment in classes) {
            if (otherEnvironment.id == classEnvironment.id) {
                throw EvaluationException(translatable("meazy:runtime.class.already_exists", classEnvironment.id))
            }
        }

        _classes.add(classEnvironment)
    }
}



fun ClassDeclarationEnvironment(parent: Environment, isShared: Boolean): ClassDeclarationEnvironment =
    ClassDeclarationEnvironmentImpl(parent, isShared)