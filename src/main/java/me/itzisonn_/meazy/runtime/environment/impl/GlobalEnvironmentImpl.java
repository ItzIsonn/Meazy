package me.itzisonn_.meazy.runtime.environment.impl;

import me.itzisonn_.meazy.parser.ast.Modifiers;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.parser.ast.expression.literal.BooleanLiteral;
import me.itzisonn_.meazy.parser.ast.expression.literal.DoubleLiteral;
import me.itzisonn_.meazy.parser.ast.expression.literal.IntLiteral;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.runtime.environment.impl.default_classes.*;
import me.itzisonn_.meazy.runtime.environment.impl.default_classes.primitives.BooleanClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.impl.default_classes.primitives.AnyClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.impl.default_classes.primitives.FloatClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.impl.default_classes.primitives.IntClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.runtime.interpreter.InvalidArgumentException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.BooleanValue;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.classes.ClassValue;
import me.itzisonn_.meazy.runtime.values.classes.DefaultClassValue;
import me.itzisonn_.meazy.runtime.values.functions.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;
import me.itzisonn_.meazy.runtime.values.number.NumberValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GlobalEnvironmentImpl extends FunctionDeclarationEnvironmentImpl implements GlobalEnvironment {
    private final List<ClassValue> classes;

    public GlobalEnvironmentImpl() {
        super(null, false);
        classes = new ArrayList<>();
    }

    @Override
    public void declareClass(ClassValue value) {
        ClassValue classValue = getClass(value.getId());
        if (classValue != null) throw new InvalidSyntaxException("Class with id " + value.getId() + " already exists!");
        classes.add(value);
    }

    @Override
    public List<ClassValue> getClasses() {
        return new ArrayList<>(classes);
    }

    public void init() {
        declareClass(new DefaultClassValue(new AnyClassEnvironment(this)) {
            @Override
            public boolean isMatches(Object value) {
                return true;
            }
        });

        declareClass(new DefaultClassValue(new BooleanClassEnvironment(this)) {
            @Override
            public boolean isMatches(Object value) {
                if (value == null) return true;
                if (value instanceof Boolean || value instanceof BooleanLiteral || value instanceof BooleanValue) return true;
                return value.toString().equals("true") || value.toString().equals("false");
            }
        });

        declareClass(new DefaultClassValue(new IntClassEnvironment(this)) {
            @Override
            public boolean isMatches(Object value) {
                if (value == null) return true;
                if (value instanceof Integer || value instanceof IntValue || value instanceof IntLiteral) return true;
                try {
                    Integer.parseInt(value.toString());
                    return true;
                }
                catch (NumberFormatException ignore) {
                    return false;
                }
            }
        });

        declareClass(new DefaultClassValue(new FloatClassEnvironment(this)) {
            @Override
            public boolean isMatches(Object value) {
                if (value == null) return true;
                if (value instanceof Number || value instanceof NumberValue || value instanceof DoubleLiteral)
                    return true;
                try {
                    Double.parseDouble(value.toString());
                    return true;
                }
                catch (NumberFormatException ignore) {
                    return false;
                }
            }
        });

        declareClass(new DefaultClassValue(new StringClassEnvironment(this, null)));
        declareClass(new DefaultClassValue(new InputClassEnvironment(this)));
        declareClass(new DefaultClassValue(new ListClassEnvironment(this)));
        declareClass(new DefaultClassValue(new MathClassEnvironment(this)));
        declareClass(new DefaultClassValue(new RandomClassEnvironment(this)));
        declareClass(new DefaultClassValue(new MeazyClassEnvironment(this)));


        declareFunction(new DefaultFunctionValue("print", List.of(
                new CallArgExpression("value", new DataType("Any", true), true)),
                null, this, Set.of(Modifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionArgs.getFirst().getFinalRuntimeValue();
                Interpreter.OUTPUT.append(value);
                System.out.print(value);
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("println", List.of(
                new CallArgExpression("value", new DataType("Any", true), true)),
                null, this, Set.of(Modifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionArgs.getFirst().getFinalRuntimeValue();
                Interpreter.OUTPUT.append(value).append("\n");
                System.out.println(value);
                return null;
            }
        });

        declareFunction(new DefaultFunctionValue("range",
                List.of(new CallArgExpression("begin", new DataType("Int", false), true), new CallArgExpression("end", new DataType("Int", false), true)),
                new DataType("List", false), this, Set.of()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue beginValue)) throw new InvalidArgumentException("Begin must be int");
                if (!(functionArgs.get(1).getFinalRuntimeValue() instanceof IntValue endValue)) throw new InvalidArgumentException("End must be int");

                List<RuntimeValue<?>> list = range(beginValue.getValue(), endValue.getValue(), 1);
                return new DefaultClassValue(new ListClassEnvironment(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), list));
            }
        });

        declareFunction(new DefaultFunctionValue("range",
                List.of(new CallArgExpression("begin", new DataType("Int", false), true), new CallArgExpression("end", new DataType("Int", false), true), new CallArgExpression("step", new DataType("Int", false), true)),
                new DataType("List", false), this, Set.of()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue beginValue)) throw new InvalidArgumentException("Begin must be int");
                if (!(functionArgs.get(1).getFinalRuntimeValue() instanceof IntValue endValue)) throw new InvalidArgumentException("End must be int");
                if (!(functionArgs.get(2).getFinalRuntimeValue() instanceof IntValue stepValue)) throw new InvalidArgumentException("Step must be int");

                if (stepValue.getValue() <= 0) throw new InvalidArgumentException("Step must be positive int");

                List<RuntimeValue<?>> list = range(beginValue.getValue(),  endValue.getValue(), stepValue.getValue());
                return new DefaultClassValue(new ListClassEnvironment(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), list));
            }
        });
    }

    private static List<RuntimeValue<?>> range(int begin, int end, int step) {
        List<RuntimeValue<?>> list = new ArrayList<>();

        if (begin < end) {
            for (int i = begin; i < end; i += step) {
                list.add(new IntValue(i));
            }
        }
        else {
            for (int i = begin; i > end; i -= step) {
                list.add(new IntValue(i));
            }
        }

        return list;
    }
}