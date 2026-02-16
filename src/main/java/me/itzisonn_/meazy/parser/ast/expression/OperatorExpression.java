package me.itzisonn_.meazy.parser.ast.expression;

import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.parser.operator.Operators;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@Getter
@NullMarked
public class OperatorExpression implements Expression {
    private final Expression left;
    @Nullable
    private final Expression right;
    private final Operator operator;

    public OperatorExpression(Expression left, @Nullable Expression right, Operator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;

        if (operator.getOperatorType() == OperatorType.INFIX) {
            if (right == null) throw new IllegalArgumentException("Expression with infix operator must have both sides");
        }
        else {
            if (right != null) throw new IllegalArgumentException("Expression with non-infix operator must have only left side");
        }
    }

    public OperatorExpression(Expression left, @Nullable Expression right, String operatorSymbol, OperatorType operatorType) {
        Operator operator = Operators.parse(operatorSymbol, operatorType);
        if (operator == null) throw new IllegalArgumentException("Unknown operator with symbol " + operatorSymbol + " and type " + operatorType);
        this(left, right, operator);
    }

    public OperatorType getOperatorType() {
        return operator.getOperatorType();
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {
        operator.emit(instructionsSet, environment, this);
    }

    @Override
    public DataType getType(Environment environment, Statement parent) {
        return operator.getType(environment, this);
    }
}
