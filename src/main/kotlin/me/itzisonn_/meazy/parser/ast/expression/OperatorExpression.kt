package me.itzisonn_.meazy.parser.ast.expression

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement
import me.itzisonn_.meazy.runtime.data.operator.Operator
import me.itzisonn_.meazy.runtime.data.operator.OperatorType
import me.itzisonn_.meazy.runtime.data.operator.Operators
import me.itzisonn_.meazy.runtime.environment.Environment

class OperatorExpression : Expression, LocalStatement {
    val left: Expression
    val right: Expression?
    val operator: Operator

    constructor(left: Expression, right: Expression?, operator: Operator) {
        this.left = left
        this.right = right
        this.operator = operator

        if (operator.operatorType == OperatorType.INFIX) {
            requireNotNull(right) { "Expression with infix operator must have both sides" }
        }
        else {
            require(right == null) { "Expression with non-infix operator must have only left side" }
        }
    }

    constructor(left: Expression, right: Expression?, operatorSymbol: String, operatorType: OperatorType)
            : this(left, right, run {
                val operator = Operators.get(operatorSymbol, operatorType)
                requireNotNull(operator) { "Unknown operator with symbol $operatorSymbol and type $operatorType" }
                operator
            })

    override fun emit(instructions: InstructionsSet, environment: Environment, parent: ProgramUnit) {
        operator.emit(instructions, environment, this)
    }

    override fun getType(environment: Environment, parent: ProgramUnit): DataType {
        return operator.getType(environment, this)
    }

    override fun alwaysReturns() = false
}
