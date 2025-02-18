package me.itzisonn_.meazy.runtime.environment.impl;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.VariableValue;
import me.itzisonn_.meazy.runtime.values.classes.constructors.ConstructorValue;

import java.util.ArrayList;
import java.util.List;

public class ClassEnvironmentImpl extends FunctionDeclarationEnvironmentImpl implements ClassEnvironment {
    @Getter
    private final String id;
    private final List<ConstructorValue> constructors;

    public ClassEnvironmentImpl(Environment parent, boolean isShared, String id) {
        super(parent, isShared);
        this.id = id;
        this.constructors = new ArrayList<>();
    }

    public ClassEnvironmentImpl(Environment parent, String id) {
        this(parent, false, id);
    }

    @Override
    public void declareVariable(VariableValue value) {
        if (getVariable(value.getId()) != null) throw new InvalidSyntaxException("Variable with id " + id + " already exists!");
        variables.add(value);
    }

    @Override
    public void declareConstructor(ConstructorValue value) {
        List<CallArgExpression> args = value.getArgs();

        main:
        for (ConstructorValue constructorValue : constructors) {
            List<CallArgExpression> callArgExpressions = constructorValue.getArgs();

            if (args.size() != callArgExpressions.size()) continue;

            for (int i = 0; i < args.size(); i++) {
                CallArgExpression callArgExpression = callArgExpressions.get(i);
                if (!callArgExpression.getDataType().equals(args.get(i).getDataType())) continue main;
            }

            throw new InvalidSyntaxException("Constructor with this args already exists!");
        }

        constructors.add(value);
    }

    @Override
    public List<ConstructorValue> getConstructors() {
        return new ArrayList<>(constructors);
    }
}