package me.itzisonn_.meazy.parser.ast.expression;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.DataTypes;

@Getter
public class CallArgExpression implements Expression {
    private final String id;
    private final DataType dataType;
    private final boolean isConstant;

    public CallArgExpression(String id, DataType dataType, boolean isConstant) {
        this.id = id;
        if (dataType != null) this.dataType = dataType;
        else this.dataType = DataTypes.ANY();
        this.isConstant = isConstant;
    }

    @Override
    public String toCodeString() {
        String declareString = isConstant ? "val" : "var";

        return declareString + " " + id + ":" + dataType.getName();
    }
}