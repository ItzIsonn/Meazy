package me.itzisonn_.meazy.runtime.data.operator.custom

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.label.GotoLabelIfComparisonTrueInstruction.ComparisonOperation
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.runtime.data.operator.Operator
import me.itzisonn_.meazy.runtime.data.operator.OperatorType
import me.itzisonn_.meazy.runtime.data.operator.compare
import me.itzisonn_.meazy.runtime.environment.Environment
import java.lang.constant.ConstantDescs

class LessOperator : Operator("less", "<", OperatorType.INFIX) {
    context(parents: ParentMap)
    override fun emit(instructions: InstructionsSet, environment: Environment, operatorExpression: OperatorExpression) {
        instructions.compare(environment, operatorExpression, ComparisonOperation.LESS)
    }

    context(parents: ParentMap)
    override fun getType(environment: Environment, operatorExpression: OperatorExpression): DataType {
        return DataType.ofNonNull(ConstantDescs.CD_boolean)
    }
}
