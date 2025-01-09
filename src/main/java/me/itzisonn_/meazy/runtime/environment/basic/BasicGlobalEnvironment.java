package me.itzisonn_.meazy.runtime.environment.basic;

import me.itzisonn_.meazy.parser.ast.AccessModifiers;
import me.itzisonn_.meazy.parser.ast.DataTypes;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.basic.default_classes.ArraysClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.basic.default_classes.InputClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.basic.default_classes.MathClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.basic.default_classes.TypesClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.environment.interfaces.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidArgumentException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidIdentifierException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.ArrayValue;
import me.itzisonn_.meazy.runtime.values.NullValue;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.clazz.ClassValue;
import me.itzisonn_.meazy.runtime.values.clazz.DefaultClassValue;
import me.itzisonn_.meazy.runtime.values.function.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.function.FunctionValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;

import java.util.*;

public class BasicGlobalEnvironment extends BasicVariableDeclarationEnvironment implements GlobalEnvironment {
    private final List<FunctionValue> functions;
    private final List<ClassValue> classes;

    public BasicGlobalEnvironment() {
        super(null, false);
        this.functions = new ArrayList<>();
        this.classes = new ArrayList<>();
        init();
    }

    @Override
    public void declareClass(String id, ClassValue value) {
        ClassValue classValue = getClass(id);
        if (classValue != null) throw new InvalidSyntaxException("Class with id " + id + " already exists!");
        classes.add(value);
    }

    @Override
    public List<ClassValue> getClasses() {
        return new ArrayList<>(classes);
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
    public FunctionDeclarationEnvironment getFunctionDeclarationEnvironment(String id, List<RuntimeValue<?>> args) {
        if (getFunction(id, args) != null) return this;
        for (FunctionValue functionValue : functions) {
            if (functionValue.getId().equals(id)) throw new InvalidIdentifierException("Function with id " + id + " exists but doesn't match args!");
        }
        throw new InvalidIdentifierException("Function with id " + id + " doesn't exist!");
    }

    @Override
    public List<FunctionValue> getFunctions() {
        return new ArrayList<>(functions);
    }

    @Override
    public boolean isShared() {
        return isShared;
    }



    private void init() {
        declareFunction(new DefaultFunctionValue("print", new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.ANY(), true))), null, this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object arg = functionArgs.getFirst().getFinalValue();
                if (arg instanceof List<?> list) {
                    StringBuilder toPrint = new StringBuilder();
                    for (int i = 0; i < list.size(); i++) {
                        Object object = list.get(i);
                        if (object instanceof RuntimeValue<?> runtimeValue) {
                            toPrint.append(runtimeValue.getFinalValue());
                        }
                        else toPrint.append(object);
                        if (i < list.size() - 1) toPrint.append(", ");
                    }
                    System.out.print(toPrint);
                }
                else System.out.print(arg);
                return null;
            }
        });

        declareFunction( new DefaultFunctionValue("println", new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.ANY(), true))), null, this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
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
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
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

                List<RuntimeValue<?>> list = new ArrayList<>();
                for (int i = begin; i < end; i++) {
                    list.add(new IntValue(i));
                }
                return new ArrayValue(list);
            }
        });


        declareClass("Input", new DefaultClassValue(new InputClassEnvironment(this)));
        declareClass("Types", new DefaultClassValue(new TypesClassEnvironment(this)));
        declareClass("Math", new DefaultClassValue(new MathClassEnvironment(this)));
        declareClass("Arrays", new DefaultClassValue(new ArraysClassEnvironment(this)));
    }
}