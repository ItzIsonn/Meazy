package me.itzisonn_.meazy.parser.operator;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

/**
 * Gives ability to create expressions faster
 */
@NullMarked
public abstract class Operator {
    /**
     * Id
     */
    private final String id;
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
     * @param id            Id
     * @param symbol        Symbol
     * @param operatorType  Operator type
     * @param isOverridable Whether this operator is overridable by operator functions
     */
    public Operator(String id, String symbol, OperatorType operatorType, boolean isOverridable) {
        this.id = id;
        this.symbol = symbol;
        this.operatorType = operatorType;
        this.isOverridable = isOverridable;
    }

    /**
     * Constructor with isOverridable set to true
     *
     * @param id           Id
     * @param symbol       Symbol
     * @param operatorType Operator type
     */
    public Operator(String id, String symbol, OperatorType operatorType) {
        this(id, symbol, operatorType, true);
    }

    /**
     * Calculates expression value with this operator
     *
     * @param instructionsSet    InstructionsSet
     * @param environment        Environment
     * @param operatorExpression Operator expression
     */
    public abstract void emit(InstructionsSet instructionsSet, Environment environment, OperatorExpression operatorExpression);

    /**
     * TODO
     *
     * @param environment        Environment
     * @param operatorExpression Operator expression
     */
    public abstract DataType getType(Environment environment, OperatorExpression operatorExpression);

    public String getId() {
        return this.id;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public OperatorType getOperatorType() {
        return this.operatorType;
    }

    public boolean isOverridable() {
        return this.isOverridable;
    }
}
