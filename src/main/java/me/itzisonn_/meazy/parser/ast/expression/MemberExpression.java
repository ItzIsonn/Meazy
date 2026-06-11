package me.itzisonn_.meazy.parser.ast.expression;

import lombok.Getter;
import lombok.Setter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

@Getter
@NullMarked
public class MemberExpression implements Expression, LocalStatement {
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
    public void emit(InstructionsSet instructions, Environment environment, ProgramUnit parent) {
        member.emit(instructions, environment, this);
    }

    @Override
    public DataType getType(Environment environment, ProgramUnit parent) {
        return member.getType(environment, this);
    }

    @Override
    public boolean alwaysReturns() {
        return false;
    }

}