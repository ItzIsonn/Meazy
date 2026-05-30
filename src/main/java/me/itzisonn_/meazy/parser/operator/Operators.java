package me.itzisonn_.meazy.parser.operator;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.NumberType;
import me.itzisonn_.meazy.instruction.label.GotoLabelIfComparisonTrueInstruction.ComparisonOperation;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.operator.custom.*;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression;
import me.itzisonn_.registry.RegistryEntry;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * Operators registrar
 * @see Registries#OPERATORS
 */
public final class Operators {
    private static boolean hasRegistered = false;

    private Operators() {}



    public static Operator POWER() {
        return parseById("power");
    }

    public static Operator NEGATION() {
        return parseById("negation");
    }

    public static Operator INVERSION() {
        return parseById("inversion");
    }



    /**
     * Finds registered Operator with given symbol and given type
     *
     * @param symbol Operator's symbol
     * @param operatorType Operator's type or null if any
     * @return Operator with given symbol or null
     */
    @Nullable
    public static Operator parse(@NonNull String symbol, @Nullable OperatorType operatorType) {
        for (RegistryEntry<Operator> entry : Registries.OPERATORS.getEntries()) {
            Operator operator = entry.getValue();
            if (symbol.equals(operator.getSymbol()) && (operatorType == null || operator.getOperatorType() == operatorType)) return operator;
        }

        return null;
    }

    /**
     * Finds registered Operator with given id
     *
     * @param id Operator's id
     * @return Operator with given id or null
     */
    @Nullable
    public static Operator parseById(@NonNull String id) {
        for (RegistryEntry<Operator> entry : Registries.OPERATORS.getEntries()) {
            Operator operator = entry.getValue();
            if (operator.getId().equals(id)) return operator;
        }

        return null;
    }



    /**
     * Initializes {@link Registries#OPERATORS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#OPERATORS} registry has already been initialized
     */
    public static void REGISTER() {
        if (hasRegistered) throw new IllegalStateException("Operators have already been initialized");
        hasRegistered = true;

        register(new AdditionOperator());
        register(new SubtractionOperator());
        register(new MultiplicationOperator());
        register(new DivisionOperator());
        register(new RemainderOperator());
        register(new PowerOperator());
        register(new NegationOperator());

        register(new AndOperator());
        register(new OrOperator());
        register(new InversionOperator());
        register(new EqualsOperator());
        register(new NotEqualsOperator());
        register(new GreaterOperator());
        register(new GreaterOrEqualsOperator());
        register(new LessOperator());
        register(new LessOrEqualsOperator());
    }

    private static void register(Operator operator) {
        Registries.OPERATORS.register(MeazyMain.getDefaultIdentifier(operator.getId()), operator);
    }

    public static void produceCompare(InstructionsSet instructionsSet, Environment environment, OperatorExpression operatorExpression, ComparisonOperation operation) {
        Expression left = operatorExpression.getLeft();
        Expression right = operatorExpression.getRight();
        if (right == null) throw new NullPointerException("Right side of operator expression is null");

        DataType leftType = left.getType(environment, operatorExpression);
        DataType rightType = right.getType(environment, operatorExpression);

        NumberType leftNumberType = NumberType.valueOf(leftType.getClassDesc());
        NumberType rightNumberType = NumberType.valueOf(rightType.getClassDesc());

        if (leftNumberType == null || rightNumberType == null) {
            throw new RuntimeException("Can't compare values " + leftType + " and " + rightType); //TODO
        }

        if (leftType.isNullable() || rightType.isNullable()) throw new RuntimeException("Can't compare nullable numbers");
        NumberType commonNumberType = NumberType.getCommonUnboxed(leftNumberType, rightNumberType);

        UUID trueLabel = instructionsSet.createAndInitLabel();
        UUID endLabel = instructionsSet.createAndInitLabel();

        left.emit(instructionsSet, environment, operatorExpression);
        instructionsSet.convertToNumberType(leftNumberType, commonNumberType);

        right.emit(instructionsSet, environment, operatorExpression);
        instructionsSet.convertToNumberType(rightNumberType, commonNumberType);

        instructionsSet.gotoLabelIfComparisonTrue(commonNumberType, operation, trueLabel);
        instructionsSet.loadConstant(0);
        instructionsSet.gotoLabel(endLabel);

        instructionsSet.bindLabel(trueLabel);
        instructionsSet.loadConstant(1);

        instructionsSet.bindLabel(endLabel);
    }
}
