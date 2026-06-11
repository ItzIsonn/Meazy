package me.itzisonn_.meazy.parser.operator.custom

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.NumberType.Companion.getCommonUnboxed
import me.itzisonn_.meazy.instruction.NumberType.Companion.valueOf
import me.itzisonn_.meazy.instruction.java
import me.itzisonn_.meazy.instruction.number.ArithmeticOperationInstruction.ArithmeticOperation
import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.DataType.Companion.of
import me.itzisonn_.meazy.parser.DataType.Companion.ofNonNull
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.operator.Operator
import me.itzisonn_.meazy.parser.operator.OperatorType
import me.itzisonn_.meazy.runtime.environment.Environment
import java.lang.constant.*

class AdditionOperator : Operator("addition", "+", OperatorType.INFIX) {
    override fun emit(instructions: InstructionsSet, environment: Environment, operatorExpression: OperatorExpression) {
        val left = operatorExpression.left
        val right = operatorExpression.right ?: error("Right side of operator expression is null")

        val leftType = left.getType(environment, operatorExpression)
        val rightType = right.getType(environment, operatorExpression)

        val leftNumberType = valueOf(leftType.classDesc)
        val rightNumberType = valueOf(rightType.classDesc)

        if (leftNumberType != null && rightNumberType != null) {
            if (leftType.isNullable || rightType.isNullable) error("Can't add nullable numbers")
            val commonNumberType = getCommonUnboxed(leftNumberType, rightNumberType)

            left.emit(instructions, environment, operatorExpression)
            instructions.convertToNumberType(leftNumberType, commonNumberType)

            right.emit(instructions, environment, operatorExpression)
            instructions.convertToNumberType(rightNumberType, commonNumberType)

            instructions.arithmeticOperation(commonNumberType, ArithmeticOperation.ADDITION)
            return
        }

        if (leftType.classDesc != ConstantDescs.CD_String && rightType.classDesc != ConstantDescs.CD_String) {
            error("Can't add values $leftType and $rightType") //TODO
        }

        left.emit(instructions, environment, operatorExpression)
        right.emit(instructions, environment, operatorExpression)

        instructions.invokeDynamicMethod(
            MethodHandleDesc.ofMethod(
                DirectMethodHandleDesc.Kind.STATIC,
                ClassDesc.of("java.lang.invoke.StringConcatFactory"),
                "makeConcatWithConstants",
                MethodTypeDesc.of(
                    ConstantDescs.CD_CallSite,
                    ConstantDescs.CD_MethodHandles_Lookup,
                    ConstantDescs.CD_String,
                    ConstantDescs.CD_MethodType,
                    ConstantDescs.CD_String,
                    ConstantDescs.CD_Object.arrayType()
                )
            ),
            "makeConcatWithConstants",
            MethodTypeDesc.of(ConstantDescs.CD_String, leftType.classDesc, rightType.classDesc),
            "\u0001\u0001".java
        )
    }

    override fun getType(environment: Environment, operatorExpression: OperatorExpression): DataType {
        val left = operatorExpression.left
        val right = operatorExpression.right ?: error("Right side of operator expression is null")

        val leftType = left.getType(environment, operatorExpression)
        val rightType = right.getType(environment, operatorExpression)

        if (leftType.classDesc == ConstantDescs.CD_String || rightType.classDesc == ConstantDescs.CD_String) {
            return of(ConstantDescs.CD_String, leftType.isNullable && rightType.isNullable)
        }

        val leftNumberType = valueOf(leftType.classDesc)
        val rightNumberType = valueOf(rightType.classDesc)

        if (leftNumberType != null && rightNumberType != null) {
            if (leftType.isNullable || rightType.isNullable) error("Can't add nullable numbers")
            return ofNonNull(getCommonUnboxed(leftNumberType, rightNumberType).classDesc)
        }

        if (leftType == rightType) return leftType
        error("Can't get type to add $leftType and $rightType TODO") //TODO
    }
}
