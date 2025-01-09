package me.itzisonn_.meazy.runtime.environment.basic;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.interfaces.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.clazz.constructor.ConstructorValue;
import me.itzisonn_.meazy.runtime.values.function.FunctionValue;

import java.util.ArrayList;
import java.util.List;

public class BasicClassEnvironment extends BasicVariableDeclarationEnvironment implements ClassEnvironment {
    @Getter
    private final String id;
    private final List<FunctionValue> functions;
    private final List<ConstructorValue> constructors;

    public BasicClassEnvironment(Environment parent, boolean isShared, String id) {
        super(parent, isShared);
        this.id = id;
        this.functions = new ArrayList<>();
        this.constructors = new ArrayList<>();
    }

    public BasicClassEnvironment(Environment parent, String id) {
        super(parent, false);
        this.id = id;
        this.functions = new ArrayList<>();
        this.constructors = new ArrayList<>();
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