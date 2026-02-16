package me.itzisonn_.meazy.parser.operator.custom;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.NumberType;
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType;
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
public class PowerOperator extends Operator {
    public PowerOperator() {
        super("power", "^", OperatorType.INFIX);
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, OperatorExpression operatorExpression) {
        Expression left = operatorExpression.getLeft();
        Expression right = operatorExpression.getRight();
        if (right == null) throw new NullPointerException("Right side of operator expression is null");

        ClassDesc leftType = left.getType(environment, operatorExpression).getClassDesc();
        ClassDesc rightType = right.getType(environment, operatorExpression).getClassDesc();

        if (!NumberType.isNumberType(leftType) || !NumberType.isNumberType(rightType)) {
            throw new RuntimeException("Can't raise to the power types " + leftType + " and " + rightType); //TODO
        }

        left.emit(instructionsSet, environment, operatorExpression);
        instructionsSet.convertToNumberType(leftType, NumberType.DOUBLE);

        right.emit(instructionsSet, environment, operatorExpression);
        instructionsSet.convertToNumberType(rightType, NumberType.DOUBLE);

        instructionsSet.invokeMethod(
                ClassDesc.of("java.lang.Math"),
                "pow",
                MethodTypeDesc.of(ConstantDescs.CD_double, ConstantDescs.CD_double, ConstantDescs.CD_double),
                _ -> {},
                InvokeType.STATIC
        );
    }

    @Override
    public DataType getType(Environment environment, OperatorExpression operatorExpression) {
        return DataType.ofNonNull(ConstantDescs.CD_double);
    }
}
