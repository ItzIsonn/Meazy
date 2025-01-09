package me.itzisonn_.meazy.runtime.environment.interfaces.declaration;

import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.values.ArrayValue;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.clazz.constructor.ConstructorValue;

import java.util.List;

/**
 * ConstructorDeclarationEnvironment adds the ability to declare and get constructors
 */
public interface ConstructorDeclarationEnvironment extends Environment {
    /**
     * Declares given constructor in this environment
     *
     * @param value Constructor value
     */
    void declareConstructor(ConstructorValue value);

    /**
     * @param args Constructor's args
     * @return Declared constructor with given args
     */
    default ConstructorValue getConstructor(List<RuntimeValue<?>> args) {
        main:
        for (ConstructorValue constructorValue : getConstructors()) {
            List<CallArgExpression> callArgExpressions = constructorValue.getArgs();

            if (args.size() != callArgExpressions.size()) continue;

            for (int i = 0; i < args.size(); i++) {
                CallArgExpression callArgExpression = callArgExpressions.get(i);
                if (callArgExpression.getArraySize() != null) {
                    if (!(args.get(i).getFinalRuntimeValue() instanceof ArrayValue arrayValue)) continue main;
                    for (RuntimeValue<?> runtimeValue : arrayValue.getValue()) {
                        if (!callArgExpression.getDataType().isMatches(runtimeValue.getFinalRuntimeValue())) continue main;
                    }
                }
                else if (!callArgExpression.getDataType().isMatches(args.get(i).getFinalRuntimeValue())) continue main;
            }


            return constructorValue;
        }

        return null;
    }

    /**
     * @return All declared constructors
     */
    List<ConstructorValue> getConstructors();

    /**
     * @return Whether has this environment at least one declared constructor
     */
    default boolean hasConstructor() {
        return !getConstructors().isEmpty();
    }
}