package me.itzisonn_.meazy.parser.operator.custom

import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.instruction.label.GotoLabelIfComparisonTrueInstruction.ComparisonOperation
import me.itzisonn_.meazy.parser.DataType
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.operator.Operator
import me.itzisonn_.meazy.parser.operator.OperatorType
import me.itzisonn_.meazy.parser.operator.compare
import me.itzisonn_.meazy.runtime.environment.Environment
import java.lang.constant.ConstantDescs

class LessOrEqualsOperator : Operator("less_or_equals", "<=", OperatorType.INFIX) {
    override fun emit(instructions: InstructionsSet, environment: Environment, operatorExpression: OperatorExpression) {
        instructions.compare(environment, operatorExpression, ComparisonOperation.LESS_OR_EQUALS)
    }

    override fun getType(environment: Environment, operatorExpression: OperatorExpression): DataType {
        return DataType.ofNonNull(ConstantDescs.CD_boolean)
    }
}
