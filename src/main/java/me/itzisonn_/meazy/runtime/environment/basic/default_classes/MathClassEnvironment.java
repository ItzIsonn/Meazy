package me.itzisonn_.meazy.runtime.environment.basic.default_classes;

import me.itzisonn_.meazy.parser.ast.AccessModifiers;
import me.itzisonn_.meazy.parser.ast.DataTypes;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.basic.BasicClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.values.functions.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.number.DoubleValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;
import me.itzisonn_.meazy.runtime.values.number.NumberValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MathClassEnvironment extends BasicClassEnvironment {
    public MathClassEnvironment(Environment parent) {
        super(parent, true, "Math");


        declareVariable("PI", DataTypes.FLOAT(), new DoubleValue(Math.PI), true, Set.of(AccessModifiers.SHARED()));
        declareVariable("E", DataTypes.FLOAT(), new DoubleValue(Math.E), true, Set.of(AccessModifiers.SHARED()));


        declareConstructor(new DefaultConstructorValue(new ArrayList<>(), this, Set.of(AccessModifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        declareFunction(new DefaultFunctionValue("round", new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT(), true))), DataTypes.INT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> numberValue)) throw new InvalidSyntaxException("Can't round non-number value");
                return new IntValue((int) Math.round(numberValue.getValue().doubleValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("floor", new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT(), true))), DataTypes.INT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> numberValue)) throw new InvalidSyntaxException("Can't round non-number value");
                return new IntValue((int) Math.floor(numberValue.getValue().doubleValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("ceil", new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT(), true))), DataTypes.INT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> numberValue)) throw new InvalidSyntaxException("Can't round non-number value");
                return new IntValue((int) Math.ceil(numberValue.getValue().doubleValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("pow",
                new ArrayList<>(List.of(new CallArgExpression("number", DataTypes.FLOAT(), true), new CallArgExpression("degree", DataTypes.FLOAT(), true))),
                DataTypes.FLOAT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> numberValue &&
                        functionArgs.get(1).getFinalRuntimeValue() instanceof NumberValue<?> degreeValue)) throw new InvalidSyntaxException("Can't get power non-number values");
                return new DoubleValue(Math.pow(numberValue.getValue().doubleValue(), degreeValue.getValue().doubleValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("abs", new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT(), true))), DataTypes.FLOAT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> number)) throw new InvalidSyntaxException("Can't get abs value of non-number value");
                return new DoubleValue(Math.abs(number.getValue().doubleValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("min",
                new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT(), true), new CallArgExpression("degree", DataTypes.FLOAT(), true))),
                DataTypes.FLOAT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> firstValue &&
                        functionArgs.get(1).getFinalRuntimeValue() instanceof NumberValue<?> secondValue)) throw new InvalidSyntaxException("Can't get min of non-number values");
                return new DoubleValue(Math.min(firstValue.getValue().doubleValue(), secondValue.getValue().doubleValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("max",
                new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT(), true), new CallArgExpression("degree", DataTypes.FLOAT(), true))),
                DataTypes.FLOAT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> firstValue &&
                        functionArgs.get(1).getFinalRuntimeValue() instanceof NumberValue<?> secondValue)) throw new InvalidSyntaxException("Can't get max of non-number values");
                return new DoubleValue(Math.max(firstValue.getValue().doubleValue(), secondValue.getValue().doubleValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("factorial", new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.INT(), true))), DataTypes.INT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue numberValue)) throw new InvalidSyntaxException("Can't get factorial of non-int value");
                int result = 1;
                for (int i = 1; i <= numberValue.getValue(); i++) {
                    result = result * i;
                }
                return new IntValue(result);
            }
        });
    }
}
