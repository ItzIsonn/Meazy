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
public class SubtractionOperator extends Operator {
    public SubtractionOperator() {
        super("subtraction", "-", OperatorType.INFIX);
    }

    @Override
    public void emit(InstructionsSet instructions, Environment environment, OperatorExpression operatorExpression) {
        Expression left = operatorExpression.getLeft();
        Expression right = operatorExpression.getRight();
        if (right == null) throw new NullPointerException("Right side of operator expression is null");

        DataType leftType = left.getType(environment, operatorExpression);
        DataType rightType = right.getType(environment, operatorExpression);

        NumberType leftNumberType = NumberType.valueOf(leftType.getClassDesc());
        NumberType rightNumberType = NumberType.valueOf(rightType.getClassDesc());

        if (leftNumberType == null || rightNumberType == null) {
            throw new RuntimeException("Can't subtract " + leftType + " and " + rightType); //TODO
        }

        if (leftType.isNullable() || rightType.isNullable()) throw new RuntimeException("Can't subtract nullable numbers");
        NumberType commonNumberType = NumberType.getCommonUnboxed(leftNumberType, rightNumberType);

        left.emit(instructions, environment, operatorExpression);
        instructions.convertToNumberType(leftNumberType, commonNumberType);

        right.emit(instructions, environment, operatorExpression);
        instructions.convertToNumberType(rightNumberType, commonNumberType);

        instructions.arithmeticOperation(commonNumberType, ArithmeticOperation.SUBTRACTION);
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

        if (leftType.isNullable() || rightType.isNullable()) throw new RuntimeException("Can't subtract nullable numbers");
        return DataType.Companion.ofNonNull(NumberType.getCommonUnboxed(leftNumberType, rightNumberType).classDesc);
    }
}
