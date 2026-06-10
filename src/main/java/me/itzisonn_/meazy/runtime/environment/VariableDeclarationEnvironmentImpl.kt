package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.EvaluationException
import me.itzisonn_.meazy.runtime.VariableValue
import me.itzisonn_.meazy.runtime.environment.impl.EnvironmentImpl
import me.itzisonn_.meazy.text.translatable

open class VariableDeclarationEnvironmentImpl(parent: Environment) : EnvironmentImpl(parent), VariableDeclarationEnvironment {
    protected val _variables = mutableListOf<VariableValue>()
    override val variables
        get() = _variables.toList()

    override fun declareVariable(id: String, type: DataType, isConstant: Boolean, value: Expression?): VariableValue {
        if (getVariable(id).isPresent) {
            throw EvaluationException(translatable("meazy:runtime.variable.already_exists", id))
        }

        val variableValue = VariableValue(id, type, isConstant, setOf(), -1, value, this)
        _variables.add(variableValue)
        return variableValue
    }

    override val isShared get() = parent.isShared
}