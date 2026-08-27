package me.itzisonn_.meazy.runtime.environment.declaration.variable

import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.EvaluationException
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.data.symbol.GlobalVariableSymbol
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.runtime.environment.EnvironmentImpl
import me.itzisonn_.meazy.util.text.translatable

/**
 * Adds to Environment ability to declare global variables
 */
interface GlobalVariableDeclarationEnvironment
    : VariableDeclarationEnvironment<GlobalVariableSymbol>



open class GlobalVariableDeclarationEnvironmentImpl(
    parent: Environment
) : EnvironmentImpl(parent), GlobalVariableDeclarationEnvironment {
    private val _variables = mutableListOf<GlobalVariableSymbol>()
    override val variables get() = _variables.toList()

    override fun declareVariable(id: String, type: DataType, isConstant: Boolean, value: Expression?, modifiers: Set<Modifier>): GlobalVariableSymbol {
        if (getVariable(id) != null) {
            throw EvaluationException(translatable("runtime.variable.already_exists", id))
        }

        val variableValue = GlobalVariableSymbol(id, type, isConstant, modifiers, value, this)
        _variables.add(variableValue)
        return variableValue
    }

    override val isShared get() = getParent().isShared
}