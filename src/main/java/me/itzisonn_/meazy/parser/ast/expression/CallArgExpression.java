package me.itzisonn_.meazy.parser.ast.expression;

import lombok.Getter;

@Getter
public class CallArgExpression implements Expression {
    private final String id;
    private final String dataType;
    private final boolean isConstant;

    public CallArgExpression(String id, String dataType, boolean isConstant) {
        this.id = id;
        if (dataType != null) this.dataType = dataType;
        else this.dataType = "any";
        this.isConstant = isConstant;
    }

    @Override
    public String toCodeString() {
        String declareString = isConstant ? "val" : "var";

        return declareString + " " + id + ":" + dataType;
    }
}