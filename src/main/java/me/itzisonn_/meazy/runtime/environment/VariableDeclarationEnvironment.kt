package me.itzisonn_.meazy.runtime.environment

import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.runtime.VariableValue
import java.util.Optional

/**
 * Adds to Environment ability to declare variables
 */
interface VariableDeclarationEnvironment : Environment {
    /**
     * Declares given VariableValue in this environment
     * @param id VariableValue TODO
     */
    fun declareVariable(id: String, type: DataType, isConstant: Boolean, value: Expression?): VariableValue?

    /**
     * @param id Variable's id
     * @return Declared variable with given id
     */
    fun getVariable(id: String) = Optional.ofNullable(variables.find { it.id == id })

    /**
     * @return All declared variables
     */
    val variables: List<VariableValue>
}