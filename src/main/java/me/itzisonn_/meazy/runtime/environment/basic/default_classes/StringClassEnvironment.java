package me.itzisonn_.meazy.runtime.environment.basic.default_classes;

import me.itzisonn_.meazy.parser.ast.AccessModifiers;
import me.itzisonn_.meazy.parser.ast.DataTypes;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.basic.BasicClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidArgumentException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidCallException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.*;
import me.itzisonn_.meazy.runtime.values.clazz.constructor.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.values.function.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringClassEnvironment extends BasicClassEnvironment {
    public StringClassEnvironment(Environment parent, String value) {
        super(parent, false, "string");


        declareVariable("value", null, DataTypes.STRING(), new InnerStringValue(value), false, Set.of(AccessModifiers.PRIVATE()));
        declareConstructor(new DefaultConstructorValue(new ArrayList<>(), this, Set.of(AccessModifiers.PRIVATE())) {
            @Override
            public void run(ArrayList<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        declareFunction(new DefaultFunctionValue("getLength", new ArrayList<>(), DataTypes.INT(), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableEnvironment("value").getVariable("value").getValue().getValue();
                if (value instanceof String string) {
                    return new IntValue(string.length());
                }
                throw new InvalidSyntaxException("Can't get length of non-string value");
            }
        });

        declareFunction(new DefaultFunctionValue("replace",
                new ArrayList<>(List.of(new CallArgExpression("target", DataTypes.STRING(), true), new CallArgExpression("replacement", DataTypes.STRING(), true))),
                DataTypes.STRING(), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableEnvironment("value").assignVariable("value",
                            new StringValue(string.replace(functionArgs.getFirst().getFinalValue().toString(), functionArgs.get(1).getFinalValue().toString())));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("replaceRegex",
                new ArrayList<>(List.of(new CallArgExpression("target", DataTypes.STRING(), true), new CallArgExpression("replacement", DataTypes.STRING(), true))),
                DataTypes.STRING(), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableEnvironment("value").assignVariable("value",
                            new StringValue(string.replaceAll(functionArgs.getFirst().getFinalValue().toString(), functionArgs.get(1).getFinalValue().toString())));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("replaceFirst",
                new ArrayList<>(List.of(new CallArgExpression("target", DataTypes.STRING(), true), new CallArgExpression("replacement", DataTypes.STRING(), true))),
                DataTypes.STRING(), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableEnvironment("value").assignVariable("value",
                            new StringValue(string.replaceFirst(functionArgs.getFirst().getFinalValue().toString(), functionArgs.get(1).getFinalValue().toString())));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("toUpperCase", new ArrayList<>(), DataTypes.STRING(), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableEnvironment("value").assignVariable("value", new StringValue(string.toUpperCase()));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("toLowerCase", new ArrayList<>(), DataTypes.STRING(), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableEnvironment("value").assignVariable("value", new StringValue(string.toLowerCase()));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("getCharAt", new ArrayList<>(List.of(new CallArgExpression("pos", DataTypes.INT(), true))), DataTypes.STRING(), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableEnvironment("value").getVariable("value").getValue().getValue();

                int pos;
                try {
                    pos = Integer.parseInt(functionArgs.getFirst().getFinalValue().toString().replaceAll("\\.0$", ""));
                }
                catch (NumberFormatException ignore) {
                    throw new InvalidArgumentException("Position must be int");
                }

                if (value instanceof String string) {
                    try {
                        return new StringValue(String.valueOf(string.charAt(pos)));
                    }
                    catch (IndexOutOfBoundsException ignore) {
                        throw new InvalidArgumentException("Index " + pos + "out of bounds " + (string.length() - 1));
                    }
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("setCharAt",
                new ArrayList<>(List.of(new CallArgExpression("pos", DataTypes.INT(), true), new CallArgExpression("char", DataTypes.STRING(), true))),
                DataTypes.STRING(), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableEnvironment("value").getVariable("value").getValue().getValue();

                int pos;
                try {
                    pos = Integer.parseInt(functionArgs.getFirst().getFinalValue().toString().replaceAll("\\.0$", ""));
                }
                catch (NumberFormatException ignore) {
                    throw new InvalidArgumentException("Position must be int");
                }

                String stringChar = functionArgs.get(1).getFinalValue().toString();
                if (stringChar.length() != 1) {
                    throw new InvalidArgumentException("Char must be one character long");
                }
                char ch = stringChar.charAt(0);

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    StringBuilder stringBuilder = new StringBuilder(string);
                    stringBuilder.setCharAt(pos, ch);
                    functionEnvironment.getVariableEnvironment("value").assignVariable("value", new StringValue(stringBuilder.toString()));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("contains", new ArrayList<>(List.of(new CallArgExpression("target", DataTypes.STRING(), true))), DataTypes.BOOLEAN(), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    return new BooleanValue(string.contains(functionArgs.getFirst().getFinalValue().toString()));
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("repeat", new ArrayList<>(List.of(new CallArgExpression("count", DataTypes.INT(), true))), DataTypes.STRING(), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableEnvironment("value").getVariable("value").getValue().getValue();

                int count;
                try {
                    count = Integer.parseInt(functionArgs.getFirst().getFinalValue().toString().replaceAll("\\.0$", ""));
                }
                catch (NumberFormatException ignore) {
                    throw new InvalidArgumentException("Count must be int");
                }

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableEnvironment("value").assignVariable("value", new StringValue(string.repeat(count)));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("trim", new ArrayList<>(), DataTypes.STRING(), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableEnvironment("value").assignVariable("value", new StringValue(string.trim()));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("startsWith", new ArrayList<>(List.of(new CallArgExpression("target", DataTypes.STRING(), true))), DataTypes.BOOLEAN(), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    return new BooleanValue(string.startsWith(functionArgs.getFirst().getFinalValue().toString()));
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("endsWith", new ArrayList<>(List.of(new CallArgExpression("target", DataTypes.STRING(), true))), DataTypes.BOOLEAN(), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    return new BooleanValue(string.endsWith(functionArgs.getFirst().getFinalValue().toString()));
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("isBlank", new ArrayList<>(), DataTypes.BOOLEAN(), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    return new BooleanValue(string.isBlank());
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("substring",
                new ArrayList<>(List.of(new CallArgExpression("begin", DataTypes.INT(), true), new CallArgExpression("end", DataTypes.INT(), true))),
                DataTypes.STRING(), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableEnvironment("value").getVariable("value").getValue().getValue();

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

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableEnvironment("value").assignVariable("value", new StringValue(string.substring(begin, end)));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("split", new ArrayList<>(List.of(new CallArgExpression("regex", DataTypes.STRING(), true))), DataTypes.STRING(), new NullValue(), this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    String[] splitString = string.split(functionArgs.getFirst().getFinalValue().toString());
                    ArrayList<RuntimeValue<?>> arrayList = new ArrayList<>();
                    for (String str : splitString) {
                        arrayList.add(new StringValue(str));
                    }
                    return new ArrayValue(arrayList);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });
    }
}