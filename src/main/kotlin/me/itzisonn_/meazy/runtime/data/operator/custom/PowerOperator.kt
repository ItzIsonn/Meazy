package me.itzisonn_.meazy.runtime.data.operator.custom

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.NumberType
import me.itzisonn_.meazy.instruction.NumberType.Companion.valueOf
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.runtime.data.operator.Operator
import me.itzisonn_.meazy.runtime.data.operator.OperatorType
import me.itzisonn_.meazy.runtime.environment.Environment
import java.lang.constant.ClassDesc
import java.lang.constant.ConstantDescs
import java.lang.constant.MethodTypeDesc

class PowerOperator : Operator("power", "^", OperatorType.INFIX) {
    context(parents: ParentMap)
    override fun emit(instructions: InstructionsSet, environment: Environment, operatorExpression: OperatorExpression) {
        val left = operatorExpression.left
        val right = operatorExpression.right ?: error("Right side of operator expression is null")

        val leftType = left.getType(environment)
        val rightType = right.getType(environment)

        val leftNumberType = valueOf(leftType.classDesc)
        val rightNumberType = valueOf(rightType.classDesc)

        if (leftNumberType == null || rightNumberType == null) error("Can't raise to a power types $leftType and $rightType") //TODO
        if (leftType.isNullable || rightType.isNullable) error("Can't rais to a power nullable numbers")

        instructions.invokeMethod(
            ClassDesc.of("java.lang.Math"),
            "pow",
            MethodTypeDesc.of(ConstantDescs.CD_double, ConstantDescs.CD_double, ConstantDescs.CD_double),
            InvokeType.STATIC
        ) {
            left.emit(this, environment)
            convertToNumberType(leftNumberType, NumberType.DOUBLE)

            right.emit(this, environment)
            convertToNumberType(rightNumberType, NumberType.DOUBLE)
        }
    }

    context(parents: ParentMap)
    override fun getType(environment: Environment, operatorExpression: OperatorExpression): DataType {
        return DataType.ofNonNull(ConstantDescs.CD_double)
    }
}
