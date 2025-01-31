package me.itzisonn_.meazy.runtime.environment.basic.default_classes;

import me.itzisonn_.meazy.parser.ast.DataType;
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

import java.util.List;
import java.util.Set;

public class MathClassEnvironment extends BasicClassEnvironment {
    public MathClassEnvironment(Environment parent) {
        super(parent, true, "Math");


        declareVariable("PI", new DataType("Float", false), new DoubleValue(Math.PI), true, Set.of("shared"));
        declareVariable("E", new DataType("Float", false), new DoubleValue(Math.E), true, Set.of("shared"));


        declareConstructor(new DefaultConstructorValue(List.of(), this, Set.of("private")) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        declareFunction(new DefaultFunctionValue("round", List.of(
                new CallArgExpression("value", new DataType("Float", false), true)),
                new DataType("Int", false), this, Set.of("shared")) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> numberValue)) throw new InvalidSyntaxException("Can't round non-number value");
                return new IntValue((int) Math.round(numberValue.getValue().doubleValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("floor", List.of(
                new CallArgExpression("value", new DataType("Float", false), true)),
                new DataType("Int", false), this, Set.of("shared")) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> numberValue)) throw new InvalidSyntaxException("Can't round non-number value");
                return new IntValue((int) Math.floor(numberValue.getValue().doubleValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("ceil", List.of(
                new CallArgExpression("value", new DataType("Float", false), true)),
                new DataType("Int", false), this, Set.of("shared")) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> numberValue)) throw new InvalidSyntaxException("Can't round non-number value");
                return new IntValue((int) Math.ceil(numberValue.getValue().doubleValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("pow", List.of(
                new CallArgExpression("number", new DataType("Float", false), true), new CallArgExpression("degree", new DataType("Float", false), true)),
                new DataType("Float", false), this, Set.of("shared")) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> numberValue &&
                        functionArgs.get(1).getFinalRuntimeValue() instanceof NumberValue<?> degreeValue)) throw new InvalidSyntaxException("Can't get power non-number values");
                return new DoubleValue(Math.pow(numberValue.getValue().doubleValue(), degreeValue.getValue().doubleValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("abs", List.of(
                new CallArgExpression("value", new DataType("Float", false), true)),
                new DataType("Float", false), this, Set.of("shared")) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> number)) throw new InvalidSyntaxException("Can't get abs value of non-number value");
                return new DoubleValue(Math.abs(number.getValue().doubleValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("min", List.of(
                new CallArgExpression("value", new DataType("Float", false), true), new CallArgExpression("degree", new DataType("Float", false), true)),
                new DataType("Float", false), this, Set.of("shared")) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> firstValue &&
                        functionArgs.get(1).getFinalRuntimeValue() instanceof NumberValue<?> secondValue)) throw new InvalidSyntaxException("Can't get min of non-number values");
                return new DoubleValue(Math.min(firstValue.getValue().doubleValue(), secondValue.getValue().doubleValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("max", List.of(
                new CallArgExpression("value", new DataType("Float", false), true), new CallArgExpression("degree", new DataType("Float", false), true)),
                new DataType("Float", false), this, Set.of("shared")) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (!(functionArgs.getFirst().getFinalRuntimeValue() instanceof NumberValue<?> firstValue &&
                        functionArgs.get(1).getFinalRuntimeValue() instanceof NumberValue<?> secondValue)) throw new InvalidSyntaxException("Can't get max of non-number values");
                return new DoubleValue(Math.max(firstValue.getValue().doubleValue(), secondValue.getValue().doubleValue()));
            }
        });

        declareFunction(new DefaultFunctionValue("factorial", List.of(
                new CallArgExpression("value", new DataType("Int", false), true)),
                new DataType("Int", false), this, Set.of("shared")) {
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
