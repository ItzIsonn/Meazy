package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.interpreter.InvalidIdentifierException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.function.FunctionValue;

import java.util.List;
import java.util.Set;

/**
 * FunctionDeclarationEnvironment adds the ability to declare and get functions
 */
public interface FunctionDeclarationEnvironment extends Environment {
    /**
     * Declares given function in this environment
     *
     * @param value FunctionValue
     */
    void declareFunction(FunctionValue value);

    /**
     * @param id Function's id
     * @param args Function's args
     * @return Declared function with given id and args
     */
    default FunctionValue getFunction(String id, List<RuntimeValue<?>> args) {
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
    default FunctionDeclarationEnvironment getFunctionDeclarationEnvironment(String id, List<RuntimeValue<?>> args) {
        if (getFunction(id, args) != null) return this;
        if (getParent() == null || !(getParent() instanceof FunctionDeclarationEnvironment functionDeclarationEnvironment)) {
            for (FunctionValue functionValue : getFunctions()) {
                if (functionValue.getId().equals(id)) throw new InvalidIdentifierException("Function with id " + id + " exists but doesn't match args!");
            }
            throw new InvalidIdentifierException("Function with id " + id + " doesn't exist!");
        }
        return functionDeclarationEnvironment.getFunctionDeclarationEnvironment(id, args);
    }
}