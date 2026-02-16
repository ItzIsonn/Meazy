package me.itzisonn_.meazy.parser.operator.custom;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType;
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
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;

@NullMarked
public class MultiplicationOperator extends Operator {
    public MultiplicationOperator() {
        super("multiplication", "*", OperatorType.INFIX);
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

        if (leftNumberType != null && rightNumberType != null) {
            NumberType commonNumberType = NumberType.getCommon(leftNumberType, rightNumberType);

            left.emit(instructionsSet, environment, operatorExpression);
            instructionsSet.convertToNumberType(leftNumberType, commonNumberType);

            right.emit(instructionsSet, environment, operatorExpression);
            instructionsSet.convertToNumberType(rightNumberType, commonNumberType);

            instructionsSet.arithmeticOperation(commonNumberType, ArithmeticOperation.MULTIPLICATION);
            return;
        }

        Expression string;
        Expression number;

        if (leftType.equals(ConstantDescs.CD_String) && rightType.equals(ConstantDescs.CD_int)) {
            string = left;
            number = right;
        }
        else if (rightType.equals(ConstantDescs.CD_String) && leftType.equals(ConstantDescs.CD_int)) {
            string = right;
            number = left;
        }
        else throw new RuntimeException("Can't multiply " + leftType + " and " + rightType + " TODO"); //TODO

        string.emit(instructionsSet, environment, operatorExpression);
        number.emit(instructionsSet, environment, operatorExpression);

        instructionsSet.invokeMethod(
                ConstantDescs.CD_String,
                "repeat",
                MethodTypeDesc.of(ConstantDescs.CD_String, ConstantDescs.CD_int),
                _ -> {},
                InvokeType.VIRTUAL
        );
    }

    @Override
    public DataType getType(Environment environment, OperatorExpression operatorExpression) {
        Expression left = operatorExpression.getLeft();
        Expression right = operatorExpression.getRight();
        if (right == null) throw new NullPointerException("Right side of operator expression is null");

        DataType leftType = left.getType(environment, operatorExpression);
        DataType rightType = right.getType(environment, operatorExpression);
        boolean isNullable = leftType.isNullable() || rightType.isNullable();

        if (leftType.getClassDesc().equals(ConstantDescs.CD_String) || rightType.getClassDesc().equals(ConstantDescs.CD_String)) {
            return DataType.of(ConstantDescs.CD_String, isNullable);
        }

        NumberType leftNumberType = NumberType.valueOf(leftType.getClassDesc());
        NumberType rightNumberType = NumberType.valueOf(rightType.getClassDesc());

        if (leftNumberType != null && rightNumberType != null) {
            return DataType.of(NumberType.getCommon(leftNumberType, rightNumberType).getClassDesc(), isNullable);
        }

        if (leftType.equals(rightType)) return leftType;
        throw new RuntimeException("Can't get type to multiply " + leftType + " and " + rightType + " TODO"); //TODO
    }
}
