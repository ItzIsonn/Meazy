package me.itzisonn_.meazy.parser.operator.custom;

import kotlin.Unit;
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

        DataType leftType = left.getType(environment, operatorExpression);
        DataType rightType = right.getType(environment, operatorExpression);

        NumberType leftNumberType = NumberType.valueOf(leftType.getClassDesc());
        NumberType rightNumberType = NumberType.valueOf(rightType.getClassDesc());

        if (leftNumberType == null || rightNumberType == null) throw new RuntimeException("Can't raise to a power types " + leftType + " and " + rightType); //TODO
        if (leftType.isNullable() || rightType.isNullable()) throw new RuntimeException("Can't rais to a power nullable numbers");

        left.emit(instructionsSet, environment, operatorExpression);
        instructionsSet.convertToNumberType(leftNumberType, NumberType.DOUBLE);

        right.emit(instructionsSet, environment, operatorExpression);
        instructionsSet.convertToNumberType(rightNumberType, NumberType.DOUBLE);

        instructionsSet.invokeMethod(
                ClassDesc.of("java.lang.Math"),
                "pow",
                MethodTypeDesc.of(ConstantDescs.CD_double, ConstantDescs.CD_double, ConstantDescs.CD_double),
                InvokeType.STATIC,
                _ -> Unit.INSTANCE
        );
    }

    @Override
    public DataType getType(Environment environment, OperatorExpression operatorExpression) {
        return DataType.ofNonNull(ConstantDescs.CD_double);
    }
}
