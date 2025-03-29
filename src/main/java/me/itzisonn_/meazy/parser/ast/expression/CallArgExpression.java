package me.itzisonn_.meazy.parser.ast.expression;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import me.itzisonn_.meazy.parser.DataType;

@Getter
@EqualsAndHashCode
public class CallArgExpression implements Expression {
    private final String id;
    private final DataType dataType;
    private final boolean isConstant;

    public CallArgExpression(String id, @NonNull DataType dataType, boolean isConstant) {
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