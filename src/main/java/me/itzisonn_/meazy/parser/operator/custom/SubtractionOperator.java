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

import java.lang.constant.ClassDesc;

@NullMarked
public class SubtractionOperator extends Operator {
    public SubtractionOperator() {
        super("subtraction", "-", OperatorType.INFIX);
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, OperatorExpression operatorExpression) {
        Expression left = operatorExpression.getLeft();
        Expression right = operatorExpression.getRight();
        if (right == null) throw new NullPointerException("Right side of operator expression is null");

        ClassDesc leftType = left.getType(environment, operatorExpression).getClassDesc();
        ClassDesc rightType = right.getType(environment, operatorExpression).getClassDesc();

        NumberType leftNumberType = NumberType.valueOf(leftType);
        NumberType rightNumberType = NumberType.valueOf(rightType);

        if (leftNumberType == null || rightNumberType == null) {
            throw new RuntimeException("Can't subtract " + leftType + " and " + rightType); //TODO
        }

        NumberType commonNumberType = NumberType.getCommon(leftNumberType, rightNumberType);

        left.emit(instructionsSet, environment, operatorExpression);
        instructionsSet.convertToNumberType(leftNumberType, commonNumberType);

        right.emit(instructionsSet, environment, operatorExpression);
        instructionsSet.convertToNumberType(rightNumberType, commonNumberType);

        instructionsSet.arithmeticOperation(commonNumberType, ArithmeticOperation.SUBTRACTION);
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
            throw new RuntimeException("Can't get type to subtract " + leftType + " and " + rightType); //TODO
        }

        return DataType.ofNonNull(NumberType.getCommon(leftNumberType, rightNumberType).getClassDesc());
    }
}
