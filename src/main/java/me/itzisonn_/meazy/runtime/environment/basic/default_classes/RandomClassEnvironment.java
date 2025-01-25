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
import java.util.Random;
import java.util.Set;

public class RandomClassEnvironment extends BasicClassEnvironment {
    public RandomClassEnvironment(Environment parent) {
        super(parent, true, "Random");


        declareVariable("value", DataTypes.ANY(), new InnerRandomValue(new Random()), false, Set.of(AccessModifiers.PRIVATE()));


        declareConstructor(new DefaultConstructorValue(new ArrayList<>(), this, Set.of()) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });

        declareConstructor(new DefaultConstructorValue(new ArrayList<>(List.of(new CallArgExpression("seed", DataTypes.INT(), true))), this, Set.of()) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {
                RuntimeValue<?> value = constructorEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Can't set seed to non-random value");

                if (!(constructorArgs.getFirst().getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't set seed to non-int value");
                randomValue.getValue().setSeed(intValue.getValue());
            }
        });


        declareFunction(new DefaultFunctionValue("randomInt", new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.INT(), true))), DataTypes.INT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Can't get random from non-random value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue intValue)) throw new InvalidSyntaxException("Can't get random of non-int value");
                return new IntValue(randomValue.getValue().nextInt(intValue.getValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("randomInt",
                new ArrayList<>(List.of(new CallArgExpression("begin", DataTypes.INT(), true), new CallArgExpression("end", DataTypes.INT(), true))),
                DataTypes.INT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Can't get random from non-random value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue beginValue &&
                        functionArgs.get(1).getFinalRuntimeValue() instanceof IntValue endValue)) throw new InvalidSyntaxException("Can't get random of non-int values");
                return new IntValue(randomValue.getValue().nextInt(beginValue.getValue(), endValue.getValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("randomFloat", new ArrayList<>(), DataTypes.FLOAT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Can't get random from non-random value");

                return new DoubleValue(randomValue.getValue().nextDouble());
            }
        });


        declareFunction(new DefaultFunctionValue("randomFloat", new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT(), true))), DataTypes.FLOAT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Can't get random from non-random value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> numberValue)) throw new InvalidSyntaxException("Can't get random of non-number value");
                return new DoubleValue(randomValue.getValue().nextDouble(numberValue.getValue().doubleValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("randomFloat",
                new ArrayList<>(List.of(new CallArgExpression("begin", DataTypes.FLOAT(), true), new CallArgExpression("end", DataTypes.FLOAT(), true))),
                DataTypes.FLOAT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                RuntimeValue<?> value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue();
                if (!(value instanceof InnerRandomValue randomValue)) throw new InvalidSyntaxException("Can't get random from non-random value");

                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof DoubleValue beginValue &&
                        functionArgs.get(1).getFinalRuntimeValue() instanceof DoubleValue endValue)) throw new InvalidSyntaxException("Can't get random of non-number values");
                return new DoubleValue(randomValue.getValue().nextDouble(beginValue.getValue(), endValue.getValue()));
            }
        });
    }

    public static class InnerRandomValue extends RuntimeValue<Random> {
        private InnerRandomValue(Random value) {
            super(value);
        }
    }
}
