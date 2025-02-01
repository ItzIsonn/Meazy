package me.itzisonn_.meazy.runtime.environment.impl;

import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.parser.ast.expression.literal.BooleanLiteral;
import me.itzisonn_.meazy.parser.ast.expression.literal.NumberLiteral;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.runtime.environment.impl.default_classes.*;
import me.itzisonn_.meazy.runtime.environment.impl.default_classes.primitives.BooleanClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.impl.default_classes.primitives.AnyClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.impl.default_classes.primitives.FloatClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.impl.default_classes.primitives.IntClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.interpreter.InvalidArgumentException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidIdentifierException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.BooleanValue;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.classes.ClassValue;
import me.itzisonn_.meazy.runtime.values.classes.DefaultClassValue;
import me.itzisonn_.meazy.runtime.values.functions.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.functions.FunctionValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;
import me.itzisonn_.meazy.runtime.values.number.NumberValue;

import java.util.*;

public class GlobalEnvironmentImpl extends VariableDeclarationEnvironmentImpl implements GlobalEnvironment {
    private final List<FunctionValue> functions;
    private final List<ClassValue> classes;

    public GlobalEnvironmentImpl() {
        super(null, false);
        this.functions = new ArrayList<>();
        this.classes = new ArrayList<>();
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

    public void init() {
        declareClass("Any", new DefaultClassValue(new AnyClassEnvironment(this)) {
            @Override
            public boolean isMatches(Object value) {
                return true;
            }
        });

        declareClass("Boolean", new DefaultClassValue(new BooleanClassEnvironment(this)) {
            @Override
            public boolean isMatches(Object value) {
                if (value == null) return true;
                if (value instanceof Boolean || value instanceof BooleanLiteral || value instanceof BooleanValue)
                    return true;
                return value.toString().equals("true") || value.toString().equals("false");
            }
        });

        declareClass("Int", new DefaultClassValue(new IntClassEnvironment(this)) {
            @Override
            public boolean isMatches(Object value) {
                if (value == null) return true;
                if (value instanceof Integer || value instanceof IntValue) return true;
                if (value instanceof NumberLiteral numberLiteral) {
                    return numberLiteral.isInt();
                }
                try {
                    Integer.parseInt(value.toString());
                    return true;
                } catch (NumberFormatException ignore) {
                    return false;
                }
            }
        });

        declareClass("Float", new DefaultClassValue(new FloatClassEnvironment(this)) {
            @Override
            public boolean isMatches(Object value) {
                if (value == null) return true;
                if (value instanceof Float || value instanceof NumberLiteral || value instanceof NumberValue)
                    return true;
                try {
                    Float.parseFloat(value.toString());
                    return true;
                } catch (NumberFormatException ignore) {
                    return false;
                }
            }
        });

        declareClass("String", new DefaultClassValue(new StringClassEnvironment(this, null)));
        declareClass("Input", new DefaultClassValue(new InputClassEnvironment(this)));
        declareClass("List", new DefaultClassValue(new ListClassEnvironment(this)));
        declareClass("Math", new DefaultClassValue(new MathClassEnvironment(this)));
        declareClass("Random", new DefaultClassValue(new RandomClassEnvironment(this)));
        declareClass("Meazy", new DefaultClassValue(new MeazyClassEnvironment(this)));


        declareFunction(new DefaultFunctionValue("print", List.of(
                new CallArgExpression("value", new DataType("Any", true), true)),
                null, this, Set.of("shared")) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionArgs.getFirst().getFinalRuntimeValue();
                Interpreter.OUTPUT.append(value);
                System.out.print(value);
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("println", List.of(
                new CallArgExpression("value", new DataType("Any", true), true)),
                null, this, Set.of("shared")) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionArgs.getFirst().getFinalRuntimeValue();
                Interpreter.OUTPUT.append(value).append("\n");
                System.out.println(value);
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("range",
                List.of(new CallArgExpression("begin", new DataType("Int", false), true), new CallArgExpression("end", new DataType("Int", false), true)),
                new DataType("List", false), this, new HashSet<>()) {
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
                return new DefaultClassValue(new ListClassEnvironment(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), list));
            }
        });
    }
}