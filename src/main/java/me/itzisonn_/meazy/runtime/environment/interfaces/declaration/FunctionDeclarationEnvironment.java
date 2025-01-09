package me.itzisonn_.meazy.runtime.environment.interfaces.declaration;

import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidIdentifierException;
import me.itzisonn_.meazy.runtime.values.ArrayValue;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.function.FunctionValue;

import java.util.List;

/**
 * FunctionDeclarationEnvironment adds the ability to declare and get functions
 */
public interface FunctionDeclarationEnvironment extends Environment {
    /**
     * Declares given function in this environment
     *
     * @param value Function value
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
                    CallArgExpression callArgExpression = callArgExpressions.get(i);
                    if (callArgExpression.getArraySize() != null) {
                        if (!(args.get(i).getFinalRuntimeValue() instanceof ArrayValue arrayValue)) continue main;
                        for (RuntimeValue<?> runtimeValue : arrayValue.getValue()) {
                            if (!callArgExpression.getDataType().isMatches(runtimeValue.getFinalRuntimeValue())) continue main;
                        }
                    }
                    else if (!callArgExpression.getDataType().isMatches(args.get(i).getFinalRuntimeValue())) continue main;
                }

                return functionValue;
            }
        }

        return null;
    }

    /**
     * @return All declared functions
     */
    List<FunctionValue> getFunctions();



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