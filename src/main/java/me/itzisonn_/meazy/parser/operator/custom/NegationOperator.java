package me.itzisonn_.meazy.parser.operator.custom;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class NegationOperator extends Operator {
    public NegationOperator() {
        super("negation", "-", OperatorType.PREFIX);
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, OperatorExpression operatorExpression) {
        Expression left = operatorExpression.getLeft();
        DataType leftType = left.getType(environment, operatorExpression);

        left.emit(instructionsSet, environment, operatorExpression);
        instructionsSet.negateNumber(leftType.getClassDesc());
    }

    @Override
    public DataType getType(Environment environment, OperatorExpression operatorExpression) {
        return operatorExpression.getLeft().getType(environment, operatorExpression);
    }
}
