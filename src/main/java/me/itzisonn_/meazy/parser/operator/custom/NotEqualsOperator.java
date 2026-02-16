package me.itzisonn_.meazy.parser.operator.custom;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.label.GotoLabelIfComparisonTrueInstruction.ComparisonOperation;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy.parser.operator.Operators;
import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ConstantDescs;

@NullMarked
public class NotEqualsOperator extends Operator {
    public NotEqualsOperator() {
        super("not_equals", "!=", OperatorType.INFIX);
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, OperatorExpression operatorExpression) {
        Operators.produceCompare(instructionsSet, environment, operatorExpression, ComparisonOperation.NOT_EQUALS);
    }
    @Override
    public DataType getType(Environment environment, OperatorExpression operatorExpression) {
        return DataType.ofNonNull(ConstantDescs.CD_boolean);
    }
}
