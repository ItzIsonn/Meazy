package me.itzisonn_.meazy.parser.operator.custom;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.number.LogicalOperationInstruction.LogicalOperation;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.util.MiscUtils;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ConstantDescs;

@NullMarked
public class OrOperator extends Operator {
    public OrOperator() {
        super("or", "||", OperatorType.INFIX);
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, OperatorExpression operatorExpression) {
        Expression left = operatorExpression.getLeft();
        Expression right = operatorExpression.getRight();
        if (right == null) throw new NullPointerException("Right side of operator expression is null");

        DataType leftType = left.getType(environment, operatorExpression);
        DataType rightType = right.getType(environment, operatorExpression);

        if (!MiscUtils.isBoolean(leftType.getClassDesc()) || !MiscUtils.isBoolean(rightType.getClassDesc())) throw new RuntimeException("Invalid operands TODO");
        if (leftType.isNullable() || rightType.isNullable()) throw new RuntimeException("Can't get logical or of nullable booleans");

        left.emit(instructionsSet, environment, operatorExpression);
        instructionsSet.convertToBooleanType(leftType.getClassDesc().equals(ConstantDescs.CD_Boolean), false);

        right.emit(instructionsSet, environment, operatorExpression);
        instructionsSet.convertToBooleanType(rightType.getClassDesc().equals(ConstantDescs.CD_Boolean), false);

        instructionsSet.logicalOperation(LogicalOperation.OR);
    }

    @Override
    public DataType getType(Environment environment, OperatorExpression operatorExpression) {
        return DataType.ofNonNull(ConstantDescs.CD_boolean);
    }
}
