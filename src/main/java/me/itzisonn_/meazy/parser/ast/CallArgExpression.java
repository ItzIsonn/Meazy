package me.itzisonn_.meazy.parser.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.data_type.DataType;

/**
 * Represents an expression that is used as arg when defining callable statements
 */
@Getter
@EqualsAndHashCode
public class CallArgExpression implements Expression {
    private final String id;
    private final DataType dataType;
    private final boolean isConstant;

    /**
     * @param id         CallArgExpression's id
     * @param dataType   CallArgExpression's DataType
     * @param isConstant Whether this CallArgExpression is constant
     * @throws NullPointerException If either id or dataType is null
     */
    public CallArgExpression(String id, DataType dataType, boolean isConstant) {
        if (id == null) throw new NullPointerException("Id can't be null");
        if (dataType == null) throw new NullPointerException("DataType can't be null");

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