package me.itzisonn_.meazy.parser.operator;

import lombok.Getter;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;

/**
 * Gives ability to create expressions faster
 */
@Getter
public abstract class Operator {
    /**
     * Symbol
     */
    private final String symbol;
    /**
     * Operator type
     */
    private final OperatorType operatorType;
    /**
     * Whether this operator is overridable by operator functions
     */
    private final boolean isOverridable;

    /**
     * Main constructor
     *
     * @param symbol Symbol
     * @param operatorType Operator type
     * @param isOverridable Whether this operator is overridable by operator functions
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
     * Constructor with isOverridable set to true
     *
     * @param symbol Symbol
     * @param operatorType Operator type
     *
     * @throws NullPointerException If either symbol or operatorType is null
     */
    public Operator(String symbol, OperatorType operatorType) throws NullPointerException {
        this(symbol, operatorType, true);
    }

    /**
     * Calculates expression value with this operator
     *
     * @param value1 First value
     * @param value2 Second value or null if this operator type isn't {@link OperatorType#INFIX}
     * @return Resulted value
     */
    public abstract RuntimeValue<?> calculate(RuntimeValue<?> value1, RuntimeValue<?> value2);
}
