package me.itzisonn_.meazy.parser.operator.custom;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.number.LogicalOperationInstruction.LogicalOperation;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ConstantDescs;

@NullMarked
public class AndOperator extends Operator {
    public AndOperator() {
        super("and", "&&", OperatorType.INFIX);
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, OperatorExpression operatorExpression) {
        Expression left = operatorExpression.getLeft();
        Expression right = operatorExpression.getRight();
        if (right == null) throw new NullPointerException("Right side of operator expression is null");

        DataType leftType = left.getType(environment, operatorExpression);
        DataType rightType = right.getType(environment, operatorExpression);
        if (!leftType.getClassDesc().equals(ConstantDescs.CD_boolean) || !rightType.getClassDesc().equals(ConstantDescs.CD_boolean)) throw new RuntimeException("Invalid operands TODO");

        left.emit(instructionsSet, environment, operatorExpression);
        right.emit(instructionsSet, environment, operatorExpression);
        instructionsSet.logicalOperation(LogicalOperation.AND);
    }

    @Override
    public DataType getType(Environment environment, OperatorExpression operatorExpression) {
        return DataType.ofNonNull(ConstantDescs.CD_boolean);
    }
}
