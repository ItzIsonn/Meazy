package me.itzisonn_.meazy.runtime.environment.basic.default_classes;

import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.parser.ast.AccessModifiers;
import me.itzisonn_.meazy.parser.ast.DataTypes;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.basic.BasicClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.function.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.number.DoubleValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MathClassEnvironment extends BasicClassEnvironment {
    public MathClassEnvironment(Environment parent) {
        super(parent, true, "Math");


        declareVariable("PI", null, DataTypes.FLOAT(), new DoubleValue(Math.PI), true, Set.of(AccessModifiers.SHARED()));
        declareVariable("E", null, DataTypes.FLOAT(), new DoubleValue(Math.E), true, Set.of(AccessModifiers.SHARED()));


        declareFunction(new DefaultFunctionValue("round", new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT(), true))), DataTypes.INT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (functionArgs.getFirst().getFinalRuntimeValue() instanceof DoubleValue doubleValue) {
                    return new IntValue((int) Math.round(doubleValue.getValue()));
                }
                throw new InvalidSyntaxException("Can't round non-number value");
            }
        });

        declareFunction(new DefaultFunctionValue("floor", new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT(), true))), DataTypes.INT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (functionArgs.getFirst().getFinalRuntimeValue() instanceof DoubleValue doubleValue) {
                    return new IntValue((int) Math.floor(doubleValue.getValue()));
                }
                throw new InvalidSyntaxException("Can't round non-number value");
            }
        });

        declareFunction(new DefaultFunctionValue("ceil", new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT(), true))), DataTypes.INT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (functionArgs.getFirst().getFinalRuntimeValue() instanceof DoubleValue doubleValue) {
                    return new IntValue((int) Math.ceil(doubleValue.getValue()));
                }
                throw new InvalidSyntaxException("Can't round non-number value");
            }
        });

        declareFunction(new DefaultFunctionValue("pow",
                new ArrayList<>(List.of(new CallArgExpression("number", DataTypes.FLOAT(), true), new CallArgExpression("degree", DataTypes.FLOAT(), true))),
                DataTypes.FLOAT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (functionArgs.getFirst().getFinalRuntimeValue() instanceof DoubleValue numberValue &&
                        functionArgs.get(1).getFinalRuntimeValue() instanceof DoubleValue degreeValue) {
                    return new DoubleValue(Math.pow(numberValue.getValue(), degreeValue.getValue()));
                }
                throw new InvalidSyntaxException("Can't get power non-number value");
            }
        });

        declareFunction(new DefaultFunctionValue("abs", new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT(), true))), DataTypes.FLOAT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (functionArgs.getFirst().getFinalRuntimeValue() instanceof DoubleValue number) {
                    return new DoubleValue(Math.abs(number.getValue()));
                }
                throw new InvalidSyntaxException("Can't get abs value of non-number value");
            }
        });

        declareFunction(new DefaultFunctionValue("min",
                new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT(), true), new CallArgExpression("degree", DataTypes.FLOAT(), true))),
                DataTypes.FLOAT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (functionArgs.getFirst().getFinalRuntimeValue() instanceof DoubleValue numberValue &&
                        functionArgs.get(1).getFinalRuntimeValue() instanceof DoubleValue degreeValue) {
                    return new DoubleValue(Math.min(numberValue.getValue(), degreeValue.getValue()));
                }
                throw new InvalidSyntaxException("Can't get min of non-number values");
            }
        });

        declareFunction(new DefaultFunctionValue("max",
                new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT(), true), new CallArgExpression("degree", DataTypes.FLOAT(), true))),
                DataTypes.FLOAT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (functionArgs.getFirst().getFinalRuntimeValue() instanceof DoubleValue numberValue &&
                        functionArgs.get(1).getFinalRuntimeValue() instanceof DoubleValue degreeValue) {
                    return new DoubleValue(Math.max(numberValue.getValue(), degreeValue.getValue()));
                }
                throw new InvalidSyntaxException("Can't get max of non-number values");
            }
        });

        declareFunction(new DefaultFunctionValue("factorial", new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.INT(), true))), DataTypes.INT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue numberValue) {
                    int result = 1;
                    for (int i = 1; i <= numberValue.getValue(); i++) {
                        result = result * i;
                    }
                    return new IntValue(result);
                }
                throw new InvalidSyntaxException("Can't get factorial of non-int value");
            }
        });

        declareFunction(new DefaultFunctionValue("randomInt", new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.INT(), true))), DataTypes.INT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue number) {
                    return new IntValue(Utils.RANDOM.nextInt(number.getValue()));
                }
                throw new InvalidSyntaxException("Can't get random of non-int value");
            }
        });

        declareFunction(new DefaultFunctionValue("randomInt",
                new ArrayList<>(List.of(new CallArgExpression("begin", DataTypes.INT(), true), new CallArgExpression("end", DataTypes.INT(), true))),
                DataTypes.INT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue beginValue &&
                        functionArgs.get(1).getFinalRuntimeValue() instanceof IntValue endValue) {
                    return new IntValue(Utils.RANDOM.nextInt(beginValue.getValue(), endValue.getValue()));
                }
                throw new InvalidSyntaxException("Can't get random of non-int values");
            }
        });

        declareFunction(new DefaultFunctionValue("randomFloat", new ArrayList<>(), DataTypes.FLOAT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return new DoubleValue(Utils.RANDOM.nextDouble());
            }
        });


        declareFunction(new DefaultFunctionValue("randomFloat", new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT(), true))), DataTypes.FLOAT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (functionArgs.getFirst().getFinalRuntimeValue() instanceof DoubleValue number) {
                    return new DoubleValue(Utils.RANDOM.nextDouble(number.getValue()));
                }
                throw new InvalidSyntaxException("Can't get random of non-number value");
            }
        });

        declareFunction(new DefaultFunctionValue("randomFloat",
                new ArrayList<>(List.of(new CallArgExpression("begin", DataTypes.FLOAT(), true), new CallArgExpression("end", DataTypes.FLOAT(), true))),
                DataTypes.FLOAT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                if (functionArgs.getFirst().getFinalRuntimeValue() instanceof DoubleValue beginValue &&
                        functionArgs.get(1).getFinalRuntimeValue() instanceof DoubleValue endValue) {
                    return new DoubleValue(Utils.RANDOM.nextDouble(beginValue.getValue(), endValue.getValue()));
                }
                throw new InvalidSyntaxException("Can't get random of non-number values");
            }
        });
    }
}
