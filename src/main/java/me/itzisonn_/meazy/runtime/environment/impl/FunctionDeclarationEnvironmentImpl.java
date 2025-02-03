package me.itzisonn_.meazy.runtime.environment.impl;

import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.functions.FunctionValue;

import java.util.ArrayList;
import java.util.List;

public abstract class FunctionDeclarationEnvironmentImpl extends EnvironmentImpl implements FunctionDeclarationEnvironment {
    private final List<FunctionValue> functions;

    public FunctionDeclarationEnvironmentImpl(Environment parent, boolean isShared) {
        super(parent, isShared);
        functions = new ArrayList<>();
    }

    @Override
    public void declareFunction(FunctionValue value) {
        List<CallArgExpression> args = value.getArgs();

        main:
        for (FunctionValue functionValue : functions) {
            if (functionValue.getId().equals(value.getId())) {
                List<CallArgExpression> callArgExpressions = functionValue.getArgs();

                if (args.size() != callArgExpressions.size()) continue;

                for (int i = 0; i < args.size(); i++) {
                    CallArgExpression callArgExpression = callArgExpressions.get(i);
                    if (!callArgExpression.getDataType().equals(args.get(i).getDataType())) continue main;
                }

                throw new InvalidSyntaxException("Function with id " + value.getId() + " already exists!");
            }
        }

        functions.add(value);
    }

    @Override
    public List<FunctionValue> getFunctions() {
        return new ArrayList<>(functions);
    }
}