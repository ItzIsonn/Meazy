package me.itzisonn_.meazy.runtime.data.operator.custom

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.NumberType.Companion.getCommonUnboxed
import me.itzisonn_.meazy.instruction.NumberType.Companion.valueOf
import me.itzisonn_.meazy.instruction.number.ArithmeticOperationInstruction.ArithmeticOperation
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.data.DataType.Companion.ofNonNull
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.runtime.data.operator.Operator
import me.itzisonn_.meazy.runtime.data.operator.OperatorType
import me.itzisonn_.meazy.runtime.environment.Environment

class DivisionOperator : Operator("division", "/", OperatorType.INFIX) {
    context(parents: ParentMap)
    override fun emit(instructions: InstructionsSet, environment: Environment, operatorExpression: OperatorExpression) {
        val left = operatorExpression.left
        val right = operatorExpression.right ?: error("Right side of operator expression is null")

        val leftType = left.getType(environment)
        val rightType = right.getType(environment)

        val leftNumberType = valueOf(leftType.classDesc)
        val rightNumberType = valueOf(rightType.classDesc)

        if (leftNumberType == null || rightNumberType == null) {
            error("Can't divide values $leftType and $rightType") //TODO
        }

        if (leftType.isNullable || rightType.isNullable) error("Can't divide nullable numbers")
        val commonNumberType = getCommonUnboxed(leftNumberType, rightNumberType)

        left.emit(instructions, environment)
        instructions.convertToNumberType(leftNumberType, commonNumberType)

        right.emit(instructions, environment)
        instructions.convertToNumberType(rightNumberType, commonNumberType)

        instructions.arithmeticOperation(commonNumberType, ArithmeticOperation.DIVISION)
    }

    context(parents: ParentMap)
    override fun getType(environment: Environment, operatorExpression: OperatorExpression): DataType {
        val left = operatorExpression.left
        val right = operatorExpression.right ?: error("Right side of operator expression is null")

        val leftType = left.getType(environment)
        val rightType = right.getType(environment)

        val leftNumberType = valueOf(leftType.classDesc)
        val rightNumberType = valueOf(rightType.classDesc)

        if (leftNumberType == null || rightNumberType == null) {
            error("Can't get type to divide $leftType and $rightType") //TODO
        }

        if (leftType.isNullable || rightType.isNullable) error("Can't divide nullable numbers")
        return ofNonNull(getCommonUnboxed(leftNumberType, rightNumberType).classDesc)
    }
}
