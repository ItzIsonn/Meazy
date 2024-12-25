package me.itzisonn_.meazy.parser.ast.expression;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.DataTypes;
import me.itzisonn_.meazy.parser.ast.expression.literal.NullLiteral;

@Getter
public class CallArgExpression implements Expression {
    private final String id;
    private final Expression arraySize;
    private final DataType dataType;
    private final boolean isConstant;

    public CallArgExpression(String id, Expression arraySize, DataType dataType, boolean isConstant) {
        this.id = id;
        this.arraySize = arraySize;
        if (dataType != null) this.dataType = dataType;
        else this.dataType = DataTypes.ANY();
        this.isConstant = isConstant;
    }

    public CallArgExpression(String id, DataType dataType, boolean isConstant) {
        this.id = id;
        this.arraySize = null;
        if (dataType != null) this.dataType = dataType;
        else this.dataType = DataTypes.ANY();
        this.isConstant = isConstant;
    }

    @Override
    public String toCodeString() {
        String declareString = isConstant ? "val" : "var";

        String arrayString;
        if (arraySize == null) arrayString = "";
        else if (arraySize instanceof NullLiteral) arrayString = "[]";
        else arrayString = "[" + arraySize.toCodeString(0) + "]";

        return declareString + " " + id + arrayString + ":" + dataType.getName();
    }
}