package me.itzisonn_.meazy.runtime.environment.basic.default_classes;

import me.itzisonn_.meazy.Utils;
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


        declareVariable("PI", DataTypes.FLOAT, new DoubleValue(Math.PI), true, Set.of("shared"));
        declareVariable("E", DataTypes.FLOAT, new DoubleValue(Math.E), true, Set.of("shared"));


        declareFunction("round", new DefaultFunctionValue(new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT, true))), DataTypes.INT, this, Set.of("shared")) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionArgs.getFirst().getFinalValue();
                if (value instanceof Number number) {
                    return new IntValue((int) Math.round(number.doubleValue()));
                }
                throw new InvalidSyntaxException("Can't get length of non-string value");
            }
        });

        declareFunction("floor", new DefaultFunctionValue(new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT, true))), DataTypes.INT, this, Set.of("shared")) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionArgs.getFirst().getFinalValue();
                if (value instanceof Number number) {
                    return new IntValue((int) Math.floor(number.doubleValue()));
                }
                throw new InvalidSyntaxException("Can't get length of non-string value");
            }
        });

        declareFunction("ceil", new DefaultFunctionValue(new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT, true))), DataTypes.INT, this, Set.of("shared")) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionArgs.getFirst().getFinalValue();
                if (value instanceof Number number) {
                    return new IntValue((int) Math.ceil(number.doubleValue()));
                }
                throw new InvalidSyntaxException("Can't get length of non-string value");
            }
        });

        declareFunction("pow", new DefaultFunctionValue(
                new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT, true), new CallArgExpression("degree", DataTypes.FLOAT, true))),
                DataTypes.FLOAT, this, Set.of("shared")) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionArgs.getFirst().getFinalValue();
                Object degree = functionArgs.get(1).getFinalValue();
                if (value instanceof Number number && degree instanceof Number degreeNumber) {
                    return new DoubleValue(Math.pow(number.doubleValue(), degreeNumber.doubleValue()));
                }
                throw new InvalidSyntaxException("Can't get length of non-string value");
            }
        });

        declareFunction("abs", new DefaultFunctionValue(new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT, true))), DataTypes.FLOAT, this, Set.of("shared")) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionArgs.getFirst().getFinalValue();
                if (value instanceof Number number) {
                    return new DoubleValue(Math.abs(number.doubleValue()));
                }
                throw new InvalidSyntaxException("Can't get length of non-string value");
            }
        });

        declareFunction("min", new DefaultFunctionValue(
                new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT, true), new CallArgExpression("degree", DataTypes.FLOAT, true))),
                DataTypes.FLOAT, this, Set.of("shared")) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionArgs.getFirst().getFinalValue();
                Object degree = functionArgs.get(1).getFinalValue();
                if (value instanceof Number number && degree instanceof Number degreeNumber) {
                    return new DoubleValue(Math.min(number.doubleValue(), degreeNumber.doubleValue()));
                }
                throw new InvalidSyntaxException("Can't get length of non-string value");
            }
        });

        declareFunction("max", new DefaultFunctionValue(
                new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT, true), new CallArgExpression("degree", DataTypes.FLOAT, true))),
                DataTypes.FLOAT, this, Set.of("shared")) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionArgs.getFirst().getFinalValue();
                Object degree = functionArgs.get(1).getFinalValue();
                if (value instanceof Number number && degree instanceof Number degreeNumber) {
                    return new DoubleValue(Math.max(number.doubleValue(), degreeNumber.doubleValue()));
                }
                throw new InvalidSyntaxException("Can't get length of non-string value");
            }
        });

        declareFunction("randomInt", new DefaultFunctionValue(new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.INT, true))), DataTypes.INT, this, Set.of("shared")) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionArgs.getFirst().getFinalValue();
                if (value instanceof Number number) {
                    return new IntValue(Utils.RANDOM.nextInt(number.intValue()));
                }
                throw new InvalidSyntaxException("Can't get length of non-string value");
            }
        });

        declareFunction("randomInt", new DefaultFunctionValue(
                new ArrayList<>(List.of(new CallArgExpression("begin", DataTypes.INT, true), new CallArgExpression("end", DataTypes.INT, true))),
                DataTypes.INT, this, Set.of("shared")) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object begin = functionArgs.getFirst().getFinalValue();
                Object end = functionArgs.get(1).getFinalValue();
                if (begin instanceof Number beginNumber && end instanceof Number endNumber) {
                    return new IntValue(Utils.RANDOM.nextInt(beginNumber.intValue(), endNumber.intValue()));
                }
                throw new InvalidSyntaxException("Can't get length of non-string value");
            }
        });

        declareFunction("randomFloat", new DefaultFunctionValue(new ArrayList<>(), DataTypes.FLOAT, this, Set.of("shared")) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return new DoubleValue(Utils.RANDOM.nextDouble());
            }
        });


        declareFunction("randomFloat", new DefaultFunctionValue(new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.FLOAT, true))), DataTypes.FLOAT, this, Set.of("shared")) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionArgs.getFirst().getFinalValue();
                if (value instanceof Number number) {
                    return new DoubleValue(Utils.RANDOM.nextDouble(number.doubleValue()));
                }
                throw new InvalidSyntaxException("Can't get length of non-string value");
            }
        });

        declareFunction("randomFloat", new DefaultFunctionValue(
                new ArrayList<>(List.of(new CallArgExpression("begin", DataTypes.FLOAT, true), new CallArgExpression("end", DataTypes.FLOAT, true))),
                DataTypes.FLOAT, this, Set.of("shared")) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object begin = functionArgs.getFirst().getFinalValue();
                Object end = functionArgs.get(1).getFinalValue();
                if (begin instanceof Number beginNumber && end instanceof Number endNumber) {
                    return new DoubleValue(Utils.RANDOM.nextDouble(beginNumber.doubleValue(), endNumber.doubleValue()));
                }
                throw new InvalidSyntaxException("Can't get length of non-string value");
            }
        });
    }
}
