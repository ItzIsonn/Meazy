package me.itzisonn_.meazy.parser.operator.custom;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ConstantDescs;
import java.util.UUID;

@NullMarked
public class InversionOperator extends Operator {
    public InversionOperator() {
        super("inversion", "!", OperatorType.PREFIX);
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, OperatorExpression operatorExpression) {
        Expression left = operatorExpression.getLeft();

        DataType leftType = left.getType(environment, operatorExpression);
        if (!leftType.getClassDesc().equals(ConstantDescs.CD_boolean)) throw new RuntimeException("Can only invert booleans TODO");

        UUID trueLabel = instructionsSet.createAndInitLabel();
        UUID endLabel = instructionsSet.createAndInitLabel();

        left.emit(instructionsSet, environment, operatorExpression);
        instructionsSet.gotoLabelIfEqualsZero(trueLabel);

        instructionsSet.loadConstant(0);
        instructionsSet.gotoLabel(endLabel);

        instructionsSet.bindLabel(trueLabel);
        instructionsSet.loadConstant(1);

        instructionsSet.bindLabel(endLabel);
    }

    @Override
    public DataType getType(Environment environment, OperatorExpression operatorExpression) {
        return DataType.ofNonNull(ConstantDescs.CD_boolean);
    }
}
