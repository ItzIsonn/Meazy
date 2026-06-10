package me.itzisonn_.meazy.runtime.environment

import java.util.Optional

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
    fun getClass(id: String): Optional<ClassEnvironment> {
        return Optional.ofNullable(classes.find { it.id == id })
    }

    /**
     * @return All declared classes
     */
    val classes: Set<ClassEnvironment>
}