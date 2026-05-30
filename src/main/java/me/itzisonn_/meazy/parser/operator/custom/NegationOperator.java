package me.itzisonn_.meazy.parser.operator.custom;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.NumberType;
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

        NumberType leftNumberType = NumberType.valueOf(leftType.getClassDesc());
        if (leftNumberType == null) throw new RuntimeException("Can't negate non-number value");
        if (leftType.isNullable()) throw new RuntimeException("Can't negate nullable number");

        left.emit(instructionsSet, environment, operatorExpression);
        instructionsSet.convertToNumberType(leftNumberType, leftNumberType.unbox());
        instructionsSet.negateNumber(leftType.getClassDesc());
    }

    @Override
    public DataType getType(Environment environment, OperatorExpression operatorExpression) {
        return operatorExpression.getLeft().getType(environment, operatorExpression);
    }
}
