package me.itzisonn_.meazy.runtime.environment.declaration

import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.runtime.EvaluationException
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.EnvironmentImpl
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment
import me.itzisonn_.meazy.text.translatable

/**
 * Adds to Environment ability to declare functions
 */
interface FunctionDeclarationEnvironment : Environment {
    /**
     * Declares given function in this environment
     * TODO
     */
    fun declareFunction(functionEnvironment: FunctionEnvironment)

    /**
     * @param id Id
     * @param args Parameters
     * @return Declared function with given id and args or null
     */
    fun getFunction(id: String, args: List<DataType>): FunctionEnvironment? {
        return functions.find { function ->
            if (function.id != id) return@find false

            val parameters = function.parameters
            if (parameters.size != args.size) return@find false

            args.forEachIndexed { i, arg ->
                val parameter = parameters[i].dataType
                if (!DataType.matches(this, arg, parameter)) return@find false
            }

            return@find true
        }
    }

    /**
     * @return All declared functions
     */
    val functions: Set<FunctionEnvironment>
}



private class FunctionDeclarationEnvironmentImpl(
    parent: Environment,
    override val isShared: Boolean
) : FunctionDeclarationEnvironment, EnvironmentImpl(parent) {
    private val _functions = mutableSetOf<FunctionEnvironment>()

    override val functions get() = _functions.toSet()

    override fun declareFunction(functionEnvironment: FunctionEnvironment) {
        val parameters = functionEnvironment.parameters

        main@ for (otherFunctionEnvironment in _functions) {
            if (otherFunctionEnvironment.id == functionEnvironment.id) {
                val otherParameters = otherFunctionEnvironment.parameters
                if (parameters.size != otherParameters.size) continue

                for (i in parameters.indices) {
                    if (otherParameters[i].dataType != parameters[i].dataType) continue@main
                }

                throw EvaluationException(translatable("meazy:runtime.function.already_exists", functionEnvironment.id))
            }
        }

        _functions.add(functionEnvironment)
    }
}



fun FunctionDeclarationEnvironment(parent: Environment, isShared: Boolean): FunctionDeclarationEnvironment =
    FunctionDeclarationEnvironmentImpl(parent, isShared)