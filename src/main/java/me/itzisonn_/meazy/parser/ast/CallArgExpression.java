package me.itzisonn_.meazy.parser.ast;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.DataType;

/**
 * Represents an expression that is used as arg when defining callable statements
 */
@Getter
@EqualsAndHashCode
public class CallArgExpression implements Expression {
    /**
     * CallArgExpression's id
     */
    private final String id;
    /**
     * CallArgExpression's DataType
     */
    private final DataType dataType;
    /**
     * Whether this CallArgExpression is constant
     */
    private final boolean isConstant;

    /**
     * CallArgExpression constructor
     *
     * @param id CallArgExpression's id
     * @param dataType CallArgExpression's DataType
     * @param isConstant Whether this CallArgExpression is constant
     * @throws NullPointerException If either id or dataType is null
     */
    public CallArgExpression(String id, DataType dataType, boolean isConstant) throws NullPointerException {
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