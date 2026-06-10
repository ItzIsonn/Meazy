package me.itzisonn_.meazy.parser.operator.custom;

import kotlin.Unit;
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

        DataType leftType = left.getType(environment, operatorExpression);
        DataType rightType = right.getType(environment, operatorExpression);

        NumberType leftNumberType = NumberType.valueOf(leftType.getClassDesc());
        NumberType rightNumberType = NumberType.valueOf(rightType.getClassDesc());

        if (leftNumberType != null && rightNumberType != null) {
            if (leftType.isNullable() || rightType.isNullable()) throw new RuntimeException("Can't multiply nullable numbers");
            NumberType commonNumberType = NumberType.getCommonUnboxed(leftNumberType, rightNumberType);

            left.emit(instructionsSet, environment, operatorExpression);
            instructionsSet.convertToNumberType(leftNumberType, commonNumberType);

            right.emit(instructionsSet, environment, operatorExpression);
            instructionsSet.convertToNumberType(rightNumberType, commonNumberType);

            instructionsSet.arithmeticOperation(commonNumberType, ArithmeticOperation.MULTIPLICATION);
            return;
        }

        Expression string;
        Expression number;
        NumberType numberType;

        if (leftType.getClassDesc().equals(ConstantDescs.CD_String) && rightNumberType != null && rightNumberType.isInt() && !rightType.isNullable()) {
            string = left;
            number = right;
            numberType = rightNumberType;
        }
        else if (rightType.getClassDesc().equals(ConstantDescs.CD_String) && leftNumberType != null && leftNumberType.isInt() && !leftType.isNullable()) {
            string = right;
            number = left;
            numberType = leftNumberType;
        }
        else throw new RuntimeException("Can't multiply " + leftType.getClassDesc() + " and " + rightType.getClassDesc() + " TODO"); //TODO

        string.emit(instructionsSet, environment, operatorExpression);
        number.emit(instructionsSet, environment, operatorExpression);
        instructionsSet.convertToNumberType(numberType, numberType.unbox());

        instructionsSet.invokeMethod(
                ConstantDescs.CD_String,
                "repeat",
                MethodTypeDesc.of(ConstantDescs.CD_String, ConstantDescs.CD_int),
                _ -> Unit.INSTANCE,
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

        if (leftType.getClassDesc().equals(ConstantDescs.CD_String) || rightType.getClassDesc().equals(ConstantDescs.CD_String)) {
            return DataType.of(ConstantDescs.CD_String, leftType.isNullable() || rightType.isNullable());
        }

        NumberType leftNumberType = NumberType.valueOf(leftType.getClassDesc());
        NumberType rightNumberType = NumberType.valueOf(rightType.getClassDesc());

        if (leftNumberType != null && rightNumberType != null) {
            if (leftType.isNullable() || rightType.isNullable()) throw new RuntimeException("Can't multiply nullable numbers");
            return DataType.ofNonNull(NumberType.getCommonUnboxed(leftNumberType, rightNumberType).classDesc);
        }

        if (leftType.equals(rightType)) return leftType;
        throw new RuntimeException("Can't get type to multiply " + leftType + " and " + rightType + " TODO"); //TODO
    }
}
