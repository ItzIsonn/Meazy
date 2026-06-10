package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.parser.DataType
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