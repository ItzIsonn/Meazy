package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.constructor.ConstructorValue;

import java.util.List;
import java.util.Set;

/**
 * Adds to Environment ability to declare constructors
 */
public interface ConstructorDeclarationEnvironment extends Environment {
    /**
     * Declares given constructor in this environment
     * @param value Constructor value
     */
    void declareConstructor(ConstructorValue value);

    /**
     * @param args Constructor's args
     * @return Declared constructor with given args or null
     * @throws NullPointerException If given args is null
     */
    default ConstructorValue getConstructor(List<RuntimeValue<?>> args) throws NullPointerException {
        if (args == null) throw new NullPointerException("Args can't be null");

        main:
        for (ConstructorValue constructorValue : getConstructors()) {
            List<CallArgExpression> callArgExpressions = constructorValue.getArgs();

            if (args.size() != callArgExpressions.size()) continue;

            for (int i = 0; i < args.size(); i++) {
                if (!callArgExpressions.get(i).getDataType().isMatches(args.get(i), getGlobalEnvironment())) continue main;
            }

            return constructorValue;
        }

        return null;
    }

    /**
     * @return All declared constructors
     */
    Set<ConstructorValue> getConstructors();

    /**
     * @return Whether this environment has at least one declared constructor
     */
    default boolean hasConstructor() {
        return !getConstructors().isEmpty();
    }
}