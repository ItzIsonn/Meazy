package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.function.FunctionValue;

import java.util.List;
import java.util.Set;

/**
 * Adds to Environment ability to declare functions
 */
public interface FunctionDeclarationEnvironment extends Environment {
    /**
     * Declares given function in this environment
     * @param value FunctionValue
     */
    void declareFunction(FunctionValue value);

    /**
     * @param id Function's id
     * @param args Function's args
     * @return Declared function with given id and args or null
     * @throws NullPointerException If either id or args is null
     */
    default FunctionValue getFunction(String id, List<RuntimeValue<?>> args) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");
        if (args == null) throw new NullPointerException("Args can't be null");

        main:
        for (FunctionValue functionValue : getFunctions()) {
            if (functionValue.getId().equals(id)) {
                List<CallArgExpression> callArgExpressions = functionValue.getArgs();

                if (args.size() != callArgExpressions.size()) continue;

                for (int i = 0; i < args.size(); i++) {
                    if (!callArgExpressions.get(i).getDataType().isMatches(args.get(i))) continue main;
                }

                return functionValue;
            }
        }

        return null;
    }

    /**
     * @return All declared functions
     */
    Set<FunctionValue> getFunctions();



    @Override
    default FunctionDeclarationEnvironment getFunctionDeclarationEnvironment(String id, List<RuntimeValue<?>> args) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");
        if (args == null) throw new NullPointerException("Args can't be null");

        if (getFunction(id, args) != null) return this;

        Environment parent = getParent();
        if (parent == null) return null;
        return parent.getFunctionDeclarationEnvironment(id, args);
    }
}