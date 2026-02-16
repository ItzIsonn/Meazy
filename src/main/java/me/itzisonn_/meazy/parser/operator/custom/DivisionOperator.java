package me.itzisonn_.meazy.parser.operator.custom;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.number.ArithmeticOperationInstruction.ArithmeticOperation;
import me.itzisonn_.meazy.instruction.NumberType;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class DivisionOperator extends Operator {
    public DivisionOperator() {
        super("division", "/", OperatorType.INFIX);
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, OperatorExpression operatorExpression) {
        Expression left = operatorExpression.getLeft();
        Expression right = operatorExpression.getRight();
        if (right == null) throw new NullPointerException("Right side of operator expression is null");

        DataType leftType = left.getType(environment, operatorExpression);
        DataType rightType = right.getType(environment, operatorExpression);

        NumberType leftNumberType = NumberType.valueOf(leftType.getClassDesc());
        NumberType rightNumberType = NumberType.valueOf(rightType.getClassDesc());

        if (leftNumberType == null || rightNumberType == null) {
            throw new RuntimeException("Can't divide " + leftType + " and " + rightType); //TODO
        }

        NumberType commonNumberType = NumberType.getCommon(leftNumberType, rightNumberType);

        left.emit(instructionsSet, environment, operatorExpression);
        instructionsSet.convertToNumberType(leftNumberType, commonNumberType);

        right.emit(instructionsSet, environment, operatorExpression);
        instructionsSet.convertToNumberType(rightNumberType, commonNumberType);

        instructionsSet.arithmeticOperation(commonNumberType, ArithmeticOperation.DIVISION);
    }

    @Override
    public DataType getType(Environment environment, OperatorExpression operatorExpression) {
        Expression left = operatorExpression.getLeft();
        Expression right = operatorExpression.getRight();
        if (right == null) throw new NullPointerException("Right side of operator expression is null");

        DataType leftType = left.getType(environment, operatorExpression);
        DataType rightType = right.getType(environment, operatorExpression);

        NumberType leftNumberType = NumberType.valueOf(leftType.getClassDesc());
        NumberType rightNumberType = NumberType.valueOf(rightType.getClassDesc());

        if (leftNumberType == null || rightNumberType == null) {
            throw new RuntimeException("Can't get type to divide " + leftType + " and " + rightType); //TODO
        }

        return DataType.ofNonNull(NumberType.getCommon(leftNumberType, rightNumberType).getClassDesc());
    }
}
