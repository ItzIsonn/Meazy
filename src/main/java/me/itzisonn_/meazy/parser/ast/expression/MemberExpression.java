package me.itzisonn_.meazy.parser.ast.expression;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberExpression implements Expression {
    private Expression object;
    private final Expression member;

    public MemberExpression(Expression object, Expression member) {
        this.object = object;
        this.member = member;
    }

    @Override
    public String toCodeString() {
        return object.toCodeString() + "." + member.toCodeString();
    }
}