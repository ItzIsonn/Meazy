package me.itzisonn_.meazy.parser.operator.custom

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.number.LogicalOperationInstruction.LogicalOperation
import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.operator.Operator
import me.itzisonn_.meazy.parser.operator.OperatorType
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.instruction.isBoolean
import java.lang.constant.ConstantDescs

class AndOperator : Operator("and", "&&", OperatorType.INFIX) {
    override fun emit(instructions: InstructionsSet, environment: Environment, operatorExpression: OperatorExpression) {
        val left = operatorExpression.left
        val right = operatorExpression.right ?: error("Right side of operator expression is null")

        val leftType = left.getType(environment, operatorExpression)
        val rightType = right.getType(environment, operatorExpression)

        if (!leftType.classDesc.isBoolean || !rightType.classDesc.isBoolean) error("Invalid operands TODO")
        if (leftType.isNullable || rightType.isNullable) error("Can't get logical and of nullable booleans")

        left.emit(instructions, environment, operatorExpression)
        instructions.convertToBooleanType(leftType.classDesc == ConstantDescs.CD_Boolean, false)

        right.emit(instructions, environment, operatorExpression)
        instructions.convertToBooleanType(rightType.classDesc == ConstantDescs.CD_Boolean, false)

        instructions.logicalOperation(LogicalOperation.AND)
    }

    override fun getType(environment: Environment, operatorExpression: OperatorExpression): DataType {
        return DataType.ofNonNull(ConstantDescs.CD_boolean)
    }
}
