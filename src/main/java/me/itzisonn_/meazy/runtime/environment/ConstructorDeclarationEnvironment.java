package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.constructors.ConstructorValue;

import java.util.List;
import java.util.Set;

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
                if (!callArgExpressions.get(i).getDataType().isMatches(args.get(i))) continue main;
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
     * @return Whether has this environment at least one declared constructor
     */
    default boolean hasConstructor() {
        return !getConstructors().isEmpty();
    }
}