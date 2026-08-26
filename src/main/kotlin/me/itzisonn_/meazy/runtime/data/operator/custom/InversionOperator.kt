package me.itzisonn_.meazy.runtime.data.operator.custom

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.runtime.data.operator.Operator
import me.itzisonn_.meazy.runtime.data.operator.OperatorType
import me.itzisonn_.meazy.runtime.environment.Environment
import me.itzisonn_.meazy.instruction.isBoolean
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.SymbolMap
import java.lang.constant.ConstantDescs

class InversionOperator : Operator("inversion", "!", OperatorType.PREFIX) {
    context(parents: ParentMap, symbols: SymbolMap)
    override fun emit(instructions: InstructionsSet, environment: Environment, operatorExpression: OperatorExpression) {
        val left = operatorExpression.left

        val leftType = left.getType(environment)
        if (!leftType.classDesc.isBoolean) error("Can only invert booleans TODO")

        val trueLabel = instructions.createAndInitLabel()
        val endLabel = instructions.createAndInitLabel()

        left.emit(instructions, environment)
        instructions.convertToBooleanType(leftType.classDesc == ConstantDescs.CD_Boolean, false)
        instructions.gotoLabelIfEqualsZero(trueLabel)

        instructions.loadConstant(0)
        instructions.gotoLabel(endLabel)

        instructions.bindLabel(trueLabel)
        instructions.loadConstant(1)

        instructions.bindLabel(endLabel)
    }

    context(parents: ParentMap)
    override fun getType(environment: Environment, operatorExpression: OperatorExpression): DataType {
        return DataType.ofNonNull(ConstantDescs.CD_boolean)
    }
}
