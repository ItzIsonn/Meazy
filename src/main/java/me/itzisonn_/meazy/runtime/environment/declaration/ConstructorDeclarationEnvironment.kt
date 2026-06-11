package me.itzisonn_.meazy.runtime.environment.declaration

import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.runtime.EvaluationException
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.EnvironmentImpl
import me.itzisonn_.meazy.text.translatable
import java.util.Optional

/**
 * Adds to Environment ability to declare constructors
 */
interface ConstructorDeclarationEnvironment : Environment {
    /**
     * Declares given constructor in this environment
     * TODO
     */
    fun declareConstructor(constructorEnvironment: ConstructorEnvironment)

    /**
     * @param args Constructor's args TODO
     * @return Declared constructor with given args or null
     */
    fun getConstructor(args: List<DataType>): Optional<ConstructorEnvironment> {
        main@ for (constructorEnvironment in constructors) {
            val parameters = constructorEnvironment.parameters
            if (args.size != parameters.size) continue

            for (i in args.indices) {
                val parameter = parameters[i].getDataType()
                val arg = args[i]
                if (!DataType.matches(this, arg, parameter)) continue@main
            }

            return Optional.of(constructorEnvironment)
        }

        return Optional.empty()
    }

    /**
     * @return Whether this environment has at least one declared constructor
     */
    fun hasConstructor(): Boolean {
        return !constructors.isEmpty()
    }

    /**
     * @return All declared constructors
     */
    val constructors: Set<ConstructorEnvironment>
}



private class ConstructorDeclarationEnvironmentImpl(
    parent: Environment,
    override val isShared: Boolean
) : ConstructorDeclarationEnvironment, EnvironmentImpl(parent) {
    private val _constructors = mutableSetOf<ConstructorEnvironment>()

    override val constructors get() = _constructors.toSet()

    override fun declareConstructor(constructorEnvironment: ConstructorEnvironment) {
        val parameters = constructorEnvironment.parameters

        main@ for (otherConstructorEnvironment in _constructors) {
            val otherParameters = otherConstructorEnvironment.parameters
            if (parameters.size != otherParameters.size) continue

            for (i in parameters.indices) {
                if (otherParameters[i].getDataType() != parameters[i].getDataType()) continue@main
            }

            throw EvaluationException(translatable("meazy:runtime.constructor.already_exists"))
        }

        _constructors.add(constructorEnvironment)
    }
}



/**
 * @param parent Parent
 * @param isShared Whether environment is shared
 * @return New constructor declaration environment
 */
fun ConstructorDeclarationEnvironment(
    parent: ClassDeclarationEnvironment, isShared: Boolean
): ConstructorDeclarationEnvironment = ConstructorDeclarationEnvironmentImpl(parent, isShared)