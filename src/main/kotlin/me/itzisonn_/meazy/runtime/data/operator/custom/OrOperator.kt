package me.itzisonn_.meazy.runtime.data.operator.custom

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.number.LogicalOperationInstruction.LogicalOperation
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.runtime.data.operator.Operator
import me.itzisonn_.meazy.runtime.data.operator.OperatorType
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.instruction.isBoolean
import me.itzisonn_.meazy.parser.ast.ParentMap
import java.lang.constant.ConstantDescs

class OrOperator : Operator("or", "||", OperatorType.INFIX) {
    context(parents: ParentMap)
    override fun emit(instructions: InstructionsSet, environment: Environment, operatorExpression: OperatorExpression) {
        val left = operatorExpression.left
        val right = operatorExpression.right ?: error("Right side of operator expression is null")

        val leftType = left.getType(environment)
        val rightType = right.getType(environment)

        if (!leftType.classDesc.isBoolean || !rightType.classDesc.isBoolean) error("Invalid operands TODO")
        if (leftType.isNullable || rightType.isNullable) error("Can't get logical or of nullable booleans")

        left.emit(instructions, environment)
        instructions.convertToBooleanType(leftType.classDesc == ConstantDescs.CD_Boolean, false)

        right.emit(instructions, environment)
        instructions.convertToBooleanType(rightType.classDesc == ConstantDescs.CD_Boolean, false)

        instructions.logicalOperation(LogicalOperation.OR)
    }

    context(parents: ParentMap)
    override fun getType(environment: Environment, operatorExpression: OperatorExpression): DataType {
        return DataType.ofNonNull(ConstantDescs.CD_boolean)
    }
}
