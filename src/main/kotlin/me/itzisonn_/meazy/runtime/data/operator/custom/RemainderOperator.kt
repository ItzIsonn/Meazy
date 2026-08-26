package me.itzisonn_.meazy.runtime.data.operator.custom

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.NumberType.Companion.getCommonUnboxed
import me.itzisonn_.meazy.instruction.NumberType.Companion.valueOf
import me.itzisonn_.meazy.instruction.number.ArithmeticOperationInstruction.ArithmeticOperation
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.runtime.data.operator.Operator
import me.itzisonn_.meazy.runtime.data.operator.OperatorType
import me.itzisonn_.meazy.runtime.environment.Environment

class RemainderOperator : Operator("remainder", "%", OperatorType.INFIX) {
    context(parents: ParentMap, symbols: SymbolMap)
    override fun emit(instructions: InstructionsSet, environment: Environment, operatorExpression: OperatorExpression) {
        val left = operatorExpression.left
        val right = operatorExpression.right ?: error("Right side of operator expression is null")

        val leftType = left.getType(environment).classDesc
        val rightType = right.getType(environment).classDesc

        val leftNumberType = valueOf(leftType)
        val rightNumberType = valueOf(rightType)

        if (leftNumberType == null || rightNumberType == null) {
            error("Can't get remainder of $leftType and $rightType") //TODO
        }

        val commonNumberType = getCommonUnboxed(leftNumberType, rightNumberType)

        left.emit(instructions, environment)
        instructions.convertToNumberType(leftNumberType, commonNumberType)

        right.emit(instructions, environment)
        instructions.convertToNumberType(rightNumberType, commonNumberType)

        instructions.arithmeticOperation(commonNumberType, ArithmeticOperation.REMAINDER)
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
            error("Can't get type to get remainder of $leftType and $rightType") //TODO
        }

        return DataType.ofNonNull(getCommonUnboxed(leftNumberType, rightNumberType).classDesc)
    }
}
