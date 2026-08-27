package me.itzisonn_.meazy.runtime.environment.declaration.variable

import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.data.symbol.VariableSymbol
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.environment.Environment

/**
 * Adds to Environment ability to declare variables
 */
sealed interface VariableDeclarationEnvironment<T : VariableSymbol> : Environment {
    /**
     * Declares given VariableValue in this environment
     * @param id VariableValue TODO
     */
    fun declareVariable(id: String, type: DataType, isConstant: Boolean, value: Expression?, modifiers: Set<Modifier>): T

    /**
     * @param id Variable's id
     * @return Declared variable with given id
     */
    fun getVariable(id: String) = variables.find { it.id == id }

    /**
     * @return All declared variables
     */
    val variables: List<T>
}