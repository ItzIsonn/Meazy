package me.itzisonn_.meazy.runtime.environment.basic;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.DataTypes;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.RuntimeClass;
import me.itzisonn_.meazy.runtime.environment.RuntimeFunction;
import me.itzisonn_.meazy.runtime.environment.basic.default_classes.InputClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.basic.default_classes.TypesClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.environment.interfaces.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.interpreter.UnknownIdentifierException;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.clazz.DefaultClassValue;
import me.itzisonn_.meazy.runtime.values.function.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.function.FunctionValue;

import java.util.*;

@Getter
public class BasicGlobalEnvironment extends BasicVariableDeclarationEnvironment implements GlobalEnvironment {
    private final ArrayList<RuntimeFunction> functions;
    private final ArrayList<RuntimeClass> classes;

    public BasicGlobalEnvironment() {
        super(null, false);
        this.functions = new ArrayList<>();
        this.classes = new ArrayList<>();
        init();
    }

    public void declareClass(String id, RuntimeValue<?> value) {
        RuntimeClass runtimeClass = getClass(id);
        if (runtimeClass != null) throw new InvalidSyntaxException("Class with id " + id + " already exists!");
        classes.add(new RuntimeClass(id, value));
    }

    public RuntimeClass getClass(String id) {
        for (RuntimeClass runtimeClass : classes) {
            if (runtimeClass.getId().equals(id)) return runtimeClass;
        }

        return null;
    }

    public void declareFunction(String id, RuntimeValue<?> value) {
        ArrayList<CallArgExpression> args;
        if (value instanceof FunctionValue functionValue) args = functionValue.getArgs();
        else if (value instanceof DefaultFunctionValue defaultFunctionValue) args = defaultFunctionValue.getArgs();
        else return;

        main:
        for (RuntimeFunction runtimeFunction : functions) {
            if (runtimeFunction.getId().equals(id)) {
                ArrayList<CallArgExpression> callArgExpressions;
                if (runtimeFunction.getFunctionValue() instanceof FunctionValue functionValue) callArgExpressions = functionValue.getArgs();
                else if (runtimeFunction.getFunctionValue() instanceof DefaultFunctionValue defaultFunctionValue)
                    callArgExpressions = defaultFunctionValue.getArgs();
                else continue;

                if (args.size() != callArgExpressions.size()) continue;

                for (int i = 0; i < args.size(); i++) {
                    CallArgExpression callArgExpression = callArgExpressions.get(i);
                    if (!callArgExpression.getDataType().equals(args.get(i).getDataType())) continue main;
                }

                throw new InvalidSyntaxException("Function with id " + id + " already exists!");
            }
        }

        functions.add(new RuntimeFunction(id, value));
    }

    public FunctionDeclarationEnvironment getFunctionEnvironment(String id, ArrayList<RuntimeValue<?>> args) {
        if (getFunction(id, args) != null) return this;
        throw new UnknownIdentifierException("Function with id " + id + " doesn't exist!");
    }

    public RuntimeValue<?> getFunction(String id, ArrayList<RuntimeValue<?>> args) {
        main:
        for (RuntimeFunction runtimeFunction : functions) {
            if (runtimeFunction.getId().equals(id)) {
                ArrayList<CallArgExpression> callArgExpressions;
                if (runtimeFunction.getFunctionValue() instanceof FunctionValue functionValue) callArgExpressions = functionValue.getArgs();
                else if (runtimeFunction.getFunctionValue() instanceof DefaultFunctionValue defaultFunctionValue)
                    callArgExpressions = defaultFunctionValue.getArgs();
                else continue;

                if (args.size() != callArgExpressions.size()) continue;

                for (int i = 0; i < args.size(); i++) {
                    CallArgExpression callArgExpression = callArgExpressions.get(i);
                    if (!callArgExpression.getDataType().isMatches(args.get(i))) continue main;
                }

                return runtimeFunction.getFunctionValue();
            }
        }

        return null;
    }

    public boolean isShared() {
        return isShared;
    }



    private void init() {
        declareFunction("print", new DefaultFunctionValue(new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.ANY, true))), null, this, Set.of("shared")) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                System.out.print(functionArgs.getFirst().getFinalValue());
                return null;
            }
        });
        declareFunction("println", new DefaultFunctionValue(new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.ANY, true))), null, this, Set.of("shared")) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                System.out.println(functionArgs.getFirst().getFinalValue());
                return null;
            }
        });

        declareClass("Input", new DefaultClassValue(new InputClassEnvironment(this)));
        //declareClass("String", new DefaultClassValue(new StringClassEnvironment(this)));
        declareClass("Types", new DefaultClassValue(new TypesClassEnvironment(this)));
    }
}