package me.itzisonn_.meazy.parser.ast.expression;

import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.util.MiscUtils;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ClassDesc;
import java.util.UUID;

@Getter
@NullMarked
public class NullCheckExpression implements Expression {
    private final Expression checkExpression;
    private final Expression nullExpression;

    public NullCheckExpression(Expression checkExpression, Expression nullExpression) {
        this.checkExpression = checkExpression;
        this.nullExpression = nullExpression;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, ProgramUnit parent) {
        DataType checkExpressionType = checkExpression.getType(environment, this);
        if (!checkExpressionType.isNullable()) {
            checkExpression.emit(instructionsSet, environment, this);
            return;
        }

        var endLabel = instructionsSet.createAndInitLabel();

        checkExpression.emit(instructionsSet, environment, this);
        instructionsSet.duplicate();
        instructionsSet.gotoLabelIfNonNull(endLabel);

        instructionsSet.pop();
        nullExpression.emit(instructionsSet, environment, this);
        ClassDesc nullExpressionClassDesc = nullExpression.getType(environment, this).getClassDesc();
        if (nullExpressionClassDesc.isPrimitive()) MiscUtils.boxPrimitive(instructionsSet, nullExpressionClassDesc);
        instructionsSet.gotoLabel(endLabel);

        instructionsSet.bindLabel(endLabel);
    }

    @Override
    public DataType getType(Environment environment, ProgramUnit parent) {
        DataType checkExpressionType = checkExpression.getType(environment, this);
        if (!checkExpressionType.isNullable()) return checkExpressionType;

        DataType nullExpressionType = nullExpression.getType(environment, this);
        ClassDesc nullExpressionClassDesc = nullExpressionType.getClassDesc();
        if (nullExpressionClassDesc.isPrimitive()) nullExpressionClassDesc = MiscUtils.getBoxedType(nullExpressionClassDesc);

        return DataType.commonOf(environment, checkExpressionType.asNonNull(), nullExpressionType.with(nullExpressionClassDesc));
    }
}
