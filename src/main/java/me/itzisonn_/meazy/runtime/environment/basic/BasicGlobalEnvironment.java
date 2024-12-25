package me.itzisonn_.meazy.runtime.environment.basic;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.AccessModifiers;
import me.itzisonn_.meazy.parser.ast.DataTypes;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.RuntimeClass;
import me.itzisonn_.meazy.runtime.environment.RuntimeFunction;
import me.itzisonn_.meazy.runtime.environment.basic.default_classes.ArraysClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.basic.default_classes.InputClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.basic.default_classes.MathClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.basic.default_classes.TypesClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.environment.interfaces.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidArgumentException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.interpreter.UnknownIdentifierException;
import me.itzisonn_.meazy.runtime.values.ArrayValue;
import me.itzisonn_.meazy.runtime.values.NullValue;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.clazz.DefaultClassValue;
import me.itzisonn_.meazy.runtime.values.function.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.function.FunctionValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;

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

    public void declareFunction(RuntimeValue<?> value) {
        String id;
        ArrayList<CallArgExpression> args;
        if (value instanceof FunctionValue functionValue) {
            id = functionValue.getId();
            args = functionValue.getArgs();
        }
        else if (value instanceof DefaultFunctionValue defaultFunctionValue) {
            id = defaultFunctionValue.getId();
            args = defaultFunctionValue.getArgs();
        }
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
        for (RuntimeFunction runtimeFunction : functions) {
            if (runtimeFunction.getId().equals(id)) throw new UnknownIdentifierException("Function with id " + id + " exists but doesn't match args!");
        }
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
                    if (callArgExpression.getArraySize() != null) {
                        if (!(args.get(i).getFinalRuntimeValue() instanceof ArrayValue arrayValue)) continue main;
                        for (RuntimeValue<?> runtimeValue : arrayValue.getValue()) {
                            if (!callArgExpression.getDataType().isMatches(runtimeValue.getFinalRuntimeValue())) continue main;
                        }
                    }
                    else if (!callArgExpression.getDataType().isMatches(args.get(i).getFinalRuntimeValue())) continue main;
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
        declareFunction(new DefaultFunctionValue("print", new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.ANY(), true))), null, this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object arg = functionArgs.getFirst().getFinalValue();
                if (arg instanceof ArrayList<?> arrayList) {
                    StringBuilder toPrint = new StringBuilder();
                    for (int i = 0; i < arrayList.size(); i++) {
                        Object object = arrayList.get(i);
                        if (object instanceof RuntimeValue<?> runtimeValue) {
                            toPrint.append(runtimeValue.getFinalValue());
                        }
                        else toPrint.append(object);
                        if (i < arrayList.size() - 1) toPrint.append(", ");
                    }
                    System.out.print(toPrint);
                }
                else System.out.print(arg);
                return null;
            }
        });

        declareFunction( new DefaultFunctionValue("println", new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.ANY(), true))), null, this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object arg = functionArgs.getFirst().getFinalValue();
                if (arg instanceof ArrayList<?> arrayList) {
                    StringBuilder toPrint = new StringBuilder();
                    for (int i = 0; i < arrayList.size(); i++) {
                        Object object = arrayList.get(i);
                        if (object instanceof RuntimeValue<?> runtimeValue) {
                            toPrint.append(runtimeValue.getFinalValue());
                        }
                        else toPrint.append(object);
                        if (i < arrayList.size() - 1) toPrint.append(", ");
                    }
                    System.out.println(toPrint);
                }
                else System.out.println(arg);
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("range",
                new ArrayList<>(List.of(new CallArgExpression("begin", DataTypes.INT(), true), new CallArgExpression("end", DataTypes.INT(), true))),
                DataTypes.INT(), new NullValue(), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                int begin;
                if (functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue beginValue) {
                    begin = beginValue.getValue();
                }
                else throw new InvalidArgumentException("Begin must be int");

                int end;
                if (functionArgs.get(1).getFinalRuntimeValue() instanceof IntValue endValue) {
                    end = endValue.getValue();
                }
                else throw new InvalidArgumentException("End must be int");

                ArrayList<RuntimeValue<?>> arrayList = new ArrayList<>();
                for (int i = begin; i < end; i++) {
                    arrayList.add(new IntValue(i));
                }
                return new ArrayValue(arrayList);
            }
        });


        declareClass("Input", new DefaultClassValue(new InputClassEnvironment(this)));
        declareClass("Types", new DefaultClassValue(new TypesClassEnvironment(this)));
        declareClass("Math", new DefaultClassValue(new MathClassEnvironment(this)));
        declareClass("Arrays", new DefaultClassValue(new ArraysClassEnvironment(this)));
    }
}