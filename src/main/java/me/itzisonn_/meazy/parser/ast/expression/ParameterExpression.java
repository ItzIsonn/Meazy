package me.itzisonn_.meazy.parser.ast.expression;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.data_type.DataType;

/**
 * Represents an expression that is used as parameter when defining callable statements
 */
@Getter
@EqualsAndHashCode
public class ParameterExpression implements Expression {
    private final String id;
    private final DataType dataType;
    private final boolean isConstant;

    /**
     * @param id         Id
     * @param dataType   Data type
     * @param isConstant Whether this parameter expression is constant
     * @throws NullPointerException If either id or dataType is null
     */
    public ParameterExpression(String id, DataType dataType, boolean isConstant) {
        if (id == null) throw new NullPointerException("Id can't be null");
        if (dataType == null) throw new NullPointerException("DataType can't be null");

        this.id = id;
        this.dataType = dataType;
        this.isConstant = isConstant;
    }

    @Override
    public String toCodeString() {
        String declareString = isConstant ? "val" : "var";
        return declareString + " " + id + " : " + dataType;
    }
}