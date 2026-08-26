package me.itzisonn_.meazy.runtime.data.operator

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.runtime.environment.Environment

/**
 * Gives ability to create expressions faster
 *
 * @param id            Id
 * @param symbol        Symbol
 * @param operatorType  Operator type
 * @param isOverridable Whether this operator is overridable by operator functions
 */
abstract class Operator(
    val id: String,
    val symbol: String,
    val operatorType: OperatorType,
    val isOverridable: Boolean = true
) {
    /**
     * Calculates expression value with this operator
     * 
     * @param instructions    InstructionsSet
     * @param environment        Environment
     * @param operatorExpression Operator expression
     */
    context(parents: ParentMap, symbols: SymbolMap)
    abstract fun emit(
        instructions: InstructionsSet,
        environment: Environment,
        operatorExpression: OperatorExpression
    )

    /**
     * TODO
     * 
     * @param environment        Environment
     * @param operatorExpression Operator expression
     */
    context(parents: ParentMap)
    abstract fun getType(environment: Environment, operatorExpression: OperatorExpression): DataType
}
