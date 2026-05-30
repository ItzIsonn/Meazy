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

import java.lang.constant.*;
import java.lang.constant.DirectMethodHandleDesc.Kind;

@NullMarked
public class AdditionOperator extends Operator {
    public AdditionOperator() {
        super("addition", "+", OperatorType.INFIX);
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
            if (leftType.isNullable() || rightType.isNullable()) throw new RuntimeException("Can't add nullable numbers");
            NumberType commonNumberType = NumberType.getCommonUnboxed(leftNumberType, rightNumberType);

            left.emit(instructionsSet, environment, operatorExpression);
            instructionsSet.convertToNumberType(leftNumberType, commonNumberType);

            right.emit(instructionsSet, environment, operatorExpression);
            instructionsSet.convertToNumberType(rightNumberType, commonNumberType);

            instructionsSet.arithmeticOperation(commonNumberType, ArithmeticOperation.ADDITION);
            return;
        }

        if (!leftType.getClassDesc().equals(ConstantDescs.CD_String) && !rightType.getClassDesc().equals(ConstantDescs.CD_String)) {
            throw new RuntimeException("Can't add values " + leftType + " and " + rightType); //TODO
        }

        left.emit(instructionsSet, environment, operatorExpression);
        right.emit(instructionsSet, environment, operatorExpression);

        instructionsSet.invokeDynamicMethod(
                MethodHandleDesc.ofMethod(
                        Kind.STATIC,
                        ClassDesc.of("java.lang.invoke.StringConcatFactory"),
                        "makeConcatWithConstants",
                        MethodTypeDesc.of(ConstantDescs.CD_CallSite, ConstantDescs.CD_MethodHandles_Lookup, ConstantDescs.CD_String, ConstantDescs.CD_MethodType, ConstantDescs.CD_String, ConstantDescs.CD_Object.arrayType())
                ),
                "makeConcatWithConstants",
                MethodTypeDesc.of(ConstantDescs.CD_String, leftType.getClassDesc(), rightType.getClassDesc()),
                "\u0001\u0001"
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
            return DataType.of(ConstantDescs.CD_String, leftType.isNullable() && rightType.isNullable());
        }

        NumberType leftNumberType = NumberType.valueOf(leftType.getClassDesc());
        NumberType rightNumberType = NumberType.valueOf(rightType.getClassDesc());

        if (leftNumberType != null && rightNumberType != null) {
            if (leftType.isNullable() || rightType.isNullable()) throw new RuntimeException("Can't add nullable numbers");
            return DataType.ofNonNull(NumberType.getCommonUnboxed(leftNumberType, rightNumberType).getClassDesc());
        }

        if (leftType.equals(rightType)) return leftType;
        throw new RuntimeException("Can't get type to add " + leftType + " and " + rightType + " TODO"); //TODO
    }
}
