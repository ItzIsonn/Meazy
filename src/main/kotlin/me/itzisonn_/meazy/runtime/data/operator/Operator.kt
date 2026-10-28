package me.itzisonn_.meazy.runtime.data.operator

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.runtime.environment.Environment

abstract class Operator(
    val id: String,
    val symbol: String,
    val operatorType: OperatorType,
    val isOverridable: Boolean = true
) {
    context(parents: ParentMap, symbols: SymbolMap)
    abstract fun emit(instructions: InstructionsSet, environment: Environment, operatorExpression: OperatorExpression)

    context(parents: ParentMap)
    abstract fun getType(environment: Environment, operatorExpression: OperatorExpression): DataType
}
