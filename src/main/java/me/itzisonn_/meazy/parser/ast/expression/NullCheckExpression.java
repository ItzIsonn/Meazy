package me.itzisonn_.meazy.parser.ast.expression;

import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

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
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {
        UUID endLabel = instructionsSet.createAndInitLabel();

        checkExpression.emit(instructionsSet, environment, this);
        instructionsSet.duplicate();
        instructionsSet.gotoLabelIfNonNull(endLabel);

        instructionsSet.pop();
        nullExpression.emit(instructionsSet, environment, this);
        instructionsSet.gotoLabel(endLabel);

        instructionsSet.bindLabel(endLabel);
    }

    @Override
    public DataType getType(Environment environment, Statement parent) {
        return DataType.commonOf(environment, checkExpression.getType(environment, this), nullExpression.getType(environment, this));
    }
}
