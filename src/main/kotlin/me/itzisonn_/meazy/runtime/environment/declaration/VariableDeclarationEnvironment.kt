package me.itzisonn_.meazy.runtime.environment.declaration

import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.EvaluationException
import me.itzisonn_.meazy.runtime.data.symbol.VariableSymbol
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.data.symbol.GlobalVariableSymbol
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.EnvironmentImpl
import me.itzisonn_.meazy.util.text.translatable

/**
 * Adds to Environment ability to declare variables
 */
interface VariableDeclarationEnvironment : Environment {
    /**
     * Declares given VariableValue in this environment
     * @param id VariableValue TODO
     */
    fun declareVariable(id: String, type: DataType, isConstant: Boolean, value: Expression?, modifiers: Set<Modifier>): VariableSymbol

    /**
     * @param id Variable's id
     * @return Declared variable with given id
     */
    fun getVariable(id: String) = variables.find { it.id == id }

    /**
     * @return All declared variables
     */
    val variables: List<VariableSymbol>
}



open class VariableDeclarationEnvironmentImpl(parent: Environment) : EnvironmentImpl(parent), VariableDeclarationEnvironment {
    protected val _variables = mutableListOf<VariableSymbol>()
    override val variables get() = _variables.toList()

    override fun declareVariable(id: String, type: DataType, isConstant: Boolean, value: Expression?, modifiers: Set<Modifier>): VariableSymbol {
        if (getVariable(id) != null) {
            throw EvaluationException(translatable("runtime.variable.already_exists", id))
        }

        val variableValue = GlobalVariableSymbol(id, type, isConstant, modifiers, value, this)
        _variables.add(variableValue)
        return variableValue
    }

    override val isShared get() = getParent().isShared
}