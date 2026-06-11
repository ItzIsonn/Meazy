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
public class LessOrEqualsOperator extends Operator {
    public LessOrEqualsOperator() {
        super("less_or_equals", "<=", OperatorType.INFIX);
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, OperatorExpression operatorExpression) {
        Operators.produceCompare(instructionsSet, environment, operatorExpression, ComparisonOperation.LESS_OR_EQUALS);
    }
    @Override
    public DataType getType(Environment environment, OperatorExpression operatorExpression) {
        return DataType.Companion.ofNonNull(ConstantDescs.CD_boolean);
    }
}
