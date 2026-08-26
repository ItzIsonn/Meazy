package me.itzisonn_.meazy.runtime.data.operator.custom

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.NumberType
import me.itzisonn_.meazy.instruction.NumberType.Companion.getCommonUnboxed
import me.itzisonn_.meazy.instruction.NumberType.Companion.valueOf
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType
import me.itzisonn_.meazy.instruction.number.ArithmeticOperationInstruction.ArithmeticOperation
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.data.DataType.Companion.of
import me.itzisonn_.meazy.runtime.data.DataType.Companion.ofNonNull
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.runtime.data.operator.Operator
import me.itzisonn_.meazy.runtime.data.operator.OperatorType
import me.itzisonn_.meazy.runtime.environment.Environment
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

class MultiplicationOperator : Operator("multiplication", "*", OperatorType.INFIX) {
    context(parents: ParentMap, symbols: SymbolMap)
    override fun emit(instructions: InstructionsSet, environment: Environment, operatorExpression: OperatorExpression) {
        val left = operatorExpression.left
        val right = operatorExpression.right ?: error("Right side of operator expression is null")

        val leftType = left.getType(environment)
        val rightType = right.getType(environment)

        val leftNumberType = valueOf(leftType.classDesc)
        val rightNumberType = valueOf(rightType.classDesc)

        if (leftNumberType != null && rightNumberType != null) {
            if (leftType.isNullable || rightType.isNullable) error("Can't multiply nullable numbers")
            val commonNumberType = getCommonUnboxed(leftNumberType, rightNumberType)

            left.emit(instructions, environment)
            instructions.convertToNumberType(leftNumberType, commonNumberType)

            right.emit(instructions, environment)
            instructions.convertToNumberType(rightNumberType, commonNumberType)

            instructions.arithmeticOperation(commonNumberType, ArithmeticOperation.MULTIPLICATION)
            return
        }

        val string: Expression?
        val number: Expression?
        val numberType: NumberType?

        if (leftType.classDesc == ConstantDescs.CD_String && rightNumberType != null && rightNumberType.isInt && !rightType.isNullable) {
            string = left
            number = right
            numberType = rightNumberType
        }
        else if (rightType.classDesc == ConstantDescs.CD_String && leftNumberType != null && leftNumberType.isInt && !leftType.isNullable) {
            string = right
            number = left
            numberType = leftNumberType
        }
        else error("Can't multiply " + leftType.classDesc + " and " + rightType.classDesc + " TODO") //TODO


        string.emit(instructions, environment)
        number.emit(instructions, environment)
        instructions.convertToNumberType(numberType, numberType.unbox())

        instructions.invokeMethod(
            ConstantDescs.CD_String,
            "repeat",
            MethodTypeDesc.of(ConstantDescs.CD_String, ConstantDescs.CD_int),
            InvokeType.VIRTUAL
        )
    }

    context(parents: ParentMap)
    override fun getType(environment: Environment, operatorExpression: OperatorExpression): DataType {
        val left = operatorExpression.left
        val right = operatorExpression.right ?: error("Right side of operator expression is null")

        val leftType = left.getType(environment)
        val rightType = right.getType(environment)

        if (leftType.classDesc == ConstantDescs.CD_String || rightType.classDesc == ConstantDescs.CD_String) {
            return of(ConstantDescs.CD_String, leftType.isNullable || rightType.isNullable)
        }

        val leftNumberType = valueOf(leftType.classDesc)
        val rightNumberType = valueOf(rightType.classDesc)

        if (leftNumberType != null && rightNumberType != null) {
            if (leftType.isNullable || rightType.isNullable) error("Can't multiply nullable numbers")
            return ofNonNull(getCommonUnboxed(leftNumberType, rightNumberType).classDesc)
        }

        if (leftType == rightType) return leftType
        error("Can't get type to multiply $leftType and $rightType TODO") //TODO
    }
}
