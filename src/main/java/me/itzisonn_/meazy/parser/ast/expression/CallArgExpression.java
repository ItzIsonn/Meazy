package me.itzisonn_.meazy.parser.ast.expression;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class CallArgExpression implements Expression {
    private final String id;
    private final String dataType;
    private final boolean isConstant;

    public CallArgExpression(String id, @NonNull String dataType, boolean isConstant) {
        this.id = id;
        this.dataType = dataType;
        this.isConstant = isConstant;
    }

    @Override
    public String toCodeString() {
        String declareString = isConstant ? "val" : "var";

        return declareString + " " + id + ":" + dataType;
    }
}