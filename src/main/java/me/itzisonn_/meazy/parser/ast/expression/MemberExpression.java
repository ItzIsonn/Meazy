package me.itzisonn_.meazy.parser.ast.expression;

import lombok.Getter;
import lombok.Setter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

@Getter
@NullMarked
public class MemberExpression implements Expression {
    @Setter
    private Expression object;
    private final Expression member;
    private final boolean isNullSafe;

    public MemberExpression(Expression object, Expression member, boolean isNullSafe) {
        this.object = object;
        this.member = member;
        this.isNullSafe = isNullSafe;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {
        member.emit(instructionsSet, environment, this);
    }

    @Override
    public DataType getType(Environment environment, Statement parent) {
        return member.getType(environment, this);
    }
}