package me.itzisonn_.meazy.parser.operator;

import lombok.Getter;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;

/**
 * Gives ability to create expressions faster
 */
@Getter
public abstract class Operator {
    /**
     * Operator's symbol
     */
    private final String symbol;
    /**
     * Operator's type
     */
    private final OperatorType operatorType;
    /**
     * Whether this Operator is overridable by operator functions
     */
    private final boolean isOverridable;

    /**
     * Operator constructor
     *
     * @param symbol Operator's symbol
     * @param operatorType Operator's type
     * @param isOverridable Whether this Operator is overridable by operator functions
     *
     * @throws NullPointerException If either symbol or operatorType is null
     */
    public Operator(String symbol, OperatorType operatorType, boolean isOverridable) throws NullPointerException {
        if (symbol == null) throw new NullPointerException("Symbol can't be null");
        if (operatorType == null) throw new NullPointerException("OperatorType can't be null");

        this.symbol = symbol;
        this.operatorType = operatorType;
        this.isOverridable = isOverridable;
    }

    /**
     * Operator constructor with isOverridable set to true
     *
     * @param symbol Operator's symbol
     * @param operatorType Operator's type
     *
     * @throws NullPointerException If either symbol or operatorType is null
     */
    public Operator(String symbol, OperatorType operatorType) throws NullPointerException {
        this(symbol, operatorType, true);
    }

    /**
     * Calculates expression value with this Operator
     *
     * @param value1 First value
     * @param value2 Second value or null if this Operator's type isn't {@link OperatorType#INFIX}
     * @return Resulted value
     */
    public abstract RuntimeValue<?> calculate(RuntimeValue<?> value1, RuntimeValue<?> value2);
}
