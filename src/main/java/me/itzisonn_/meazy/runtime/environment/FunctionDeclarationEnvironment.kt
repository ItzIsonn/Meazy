package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.runtime.EvaluationException
import me.itzisonn_.meazy.runtime.environment.impl.EnvironmentImpl
import me.itzisonn_.meazy.text.translatable
import java.util.Optional

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
    fun getFunction(id: String, args: List<DataType>): Optional<FunctionEnvironment> {
        main@ for (functionEnvironment in functions) {
            if (functionEnvironment.id != id) continue

            val parameters = functionEnvironment.parameters
            if (parameters.size != args.size) continue

            for (i in args.indices) {
                val parameter = parameters[i].getDataType()
                val arg = args[i]
                if (!DataType.matches(this, arg, parameter)) continue@main
            }

            return Optional.of(functionEnvironment)
        }

        return Optional.empty()
    }

    /**
     * @return All declared functions
     */
    val functions: Set<FunctionEnvironment>
}



abstract class FunctionDeclarationEnvironmentImpl(
    parent: Environment,
    override val isShared: Boolean
) : EnvironmentImpl(parent),
    FunctionDeclarationEnvironment {
    private val _functions = mutableSetOf<FunctionEnvironment>()

    override val functions get() = _functions.toSet()

    override fun declareFunction(functionEnvironment: FunctionEnvironment) {
        val parameters = functionEnvironment.parameters

        main@ for (otherFunctionEnvironment in _functions) {
            if (otherFunctionEnvironment.id == functionEnvironment.id) {
                val otherParameters = otherFunctionEnvironment.parameters
                if (parameters.size != otherParameters.size) continue

                for (i in parameters.indices) {
                    if (otherParameters[i].getDataType() != parameters[i].getDataType()) continue@main
                }

                throw EvaluationException(translatable("meazy:runtime.function.already_exists", functionEnvironment.id))
            }
        }

        _functions.add(functionEnvironment)
    }
}