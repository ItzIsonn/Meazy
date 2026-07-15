package me.itzisonn_.meazy.runtime.data.operator.custom

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.NumberType.Companion.valueOf
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.data.DataType.Companion.ofNonNull
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.runtime.data.operator.Operator
import me.itzisonn_.meazy.runtime.data.operator.OperatorType
import me.itzisonn_.meazy.runtime.environment.Environment

class NegationOperator : Operator("negation", "-", OperatorType.PREFIX) {
    context(parents: ParentMap)
    override fun emit(instructions: InstructionsSet, environment: Environment, operatorExpression: OperatorExpression) {
        val left = operatorExpression.left
        val leftType = left.getType(environment)

        val leftNumberType = valueOf(leftType.classDesc) ?: error("Can't negate non-number value")
        if (leftType.isNullable) error("Can't negate nullable number")

        left.emit(instructions, environment)
        instructions.convertToNumberType(leftNumberType, leftNumberType.unbox())
        instructions.negateNumber(leftNumberType)
    }

    context(parents: ParentMap)
    override fun getType(environment: Environment, operatorExpression: OperatorExpression): DataType {
        val left = operatorExpression.left
        val leftType = left.getType(environment)

        val leftNumberType = valueOf(leftType.classDesc) ?: error("Can't negate non-number value")
        if (leftType.isNullable) error("Can't negate nullable number")

        return ofNonNull(leftNumberType.unbox().classDesc)
    }
}
