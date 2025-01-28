package me.itzisonn_.meazy.runtime.environment.basic.default_classes;

import me.itzisonn_.meazy.parser.ast.AccessModifiers;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.runtime.environment.basic.BasicClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidArgumentException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidCallException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.*;
import me.itzisonn_.meazy.runtime.values.classes.DefaultClassValue;
import me.itzisonn_.meazy.runtime.values.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.values.functions.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringClassEnvironment extends BasicClassEnvironment {
    public StringClassEnvironment(Environment parent, String value) {
        super(parent, false, "string");


        declareVariable("value", "any", new InnerStringValue(value), false, Set.of(AccessModifiers.PRIVATE()));
        declareConstructor(new DefaultConstructorValue(new ArrayList<>(), this, Set.of(AccessModifiers.PRIVATE())) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {}
        });


        declareFunction(new DefaultFunctionValue("getLength", new ArrayList<>(), "int", this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();
                if (value instanceof String string) {
                    return new IntValue(string.length());
                }
                throw new InvalidSyntaxException("Can't get length of non-string value");
            }
        });

        declareFunction(new DefaultFunctionValue("replace",
                new ArrayList<>(List.of(new CallArgExpression("target", "string", true), new CallArgExpression("replacement", "string", true))),
                "string", this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value",
                            new StringValue(string.replace(functionArgs.getFirst().getFinalValue().toString(), functionArgs.get(1).getFinalValue().toString())));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("replaceRegex",
                new ArrayList<>(List.of(new CallArgExpression("target", "string", true), new CallArgExpression("replacement", "string", true))),
                "string", this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value",
                            new StringValue(string.replaceAll(functionArgs.getFirst().getFinalValue().toString(), functionArgs.get(1).getFinalValue().toString())));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("replaceFirst",
                new ArrayList<>(List.of(new CallArgExpression("target", "string", true), new CallArgExpression("replacement", "string", true))),
                "string", this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value",
                            new StringValue(string.replaceFirst(functionArgs.getFirst().getFinalValue().toString(), functionArgs.get(1).getFinalValue().toString())));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("toUpperCase", new ArrayList<>(), "string", this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value", new StringValue(string.toUpperCase()));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("toLowerCase", new ArrayList<>(), "string", this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value", new StringValue(string.toLowerCase()));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("getCharAt", new ArrayList<>(List.of(new CallArgExpression("pos", "int", true))), "string", this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    if (functionArgs.getFirst().getFinalRuntimeValue() instanceof IntValue intValue) {
                        try {
                            return new StringValue(String.valueOf(string.charAt(intValue.getValue())));
                        }
                        catch (IndexOutOfBoundsException ignore) {
                            throw new InvalidArgumentException("Index " + intValue.getValue() + "out of bounds " + (string.length() - 1));
                        }
                    }
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("setCharAt",
                new ArrayList<>(List.of(new CallArgExpression("pos", "int", true), new CallArgExpression("char", "string", true))),
                "string", this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

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
                    functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value", new StringValue(stringBuilder.toString()));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("contains", new ArrayList<>(List.of(new CallArgExpression("target", "string", true))), "boolean", this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    return new BooleanValue(string.contains(functionArgs.getFirst().getFinalValue().toString()));
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("startsWith", new ArrayList<>(List.of(new CallArgExpression("target", "string", true))), "boolean", this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    return new BooleanValue(string.startsWith(functionArgs.getFirst().getFinalValue().toString()));
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("endsWith", new ArrayList<>(List.of(new CallArgExpression("target", "string", true))), "boolean", this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    return new BooleanValue(string.endsWith(functionArgs.getFirst().getFinalValue().toString()));
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("repeat", new ArrayList<>(List.of(new CallArgExpression("count", "int", true))), "string", this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

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
                    functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value", new StringValue(string.repeat(count)));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("trim", new ArrayList<>(), "string", this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    if (!(getParentEnvironment() instanceof StringClassEnvironment classEnvironment)) {
                        throw new InvalidCallException("Invalid function call");
                    }
                    functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value", new StringValue(string.trim()));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("isBlank", new ArrayList<>(), "boolean", this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    return new BooleanValue(string.isBlank());
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("substring",
                new ArrayList<>(List.of(new CallArgExpression("begin", "int", true), new CallArgExpression("end", "int", true))),
                "string", this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

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
                    functionEnvironment.getVariableDeclarationEnvironment("value").assignVariable("value", new StringValue(string.substring(begin, end)));
                    return new StringValue(classEnvironment);
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });

        declareFunction(new DefaultFunctionValue("split", new ArrayList<>(List.of(new CallArgExpression("regex", "string", true))), "string", this, new HashSet<>()) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionEnvironment.getVariableDeclarationEnvironment("value").getVariable("value").getValue().getValue();

                if (value instanceof String string) {
                    String[] splitString = string.split(functionArgs.getFirst().getFinalValue().toString());
                    List<RuntimeValue<?>> list = new ArrayList<>();
                    for (String str : splitString) {
                        list.add(new StringValue(str));
                    }
                    return new DefaultClassValue(new ListClassEnvironment(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), list));
                }
                throw new InvalidSyntaxException("Value must be string");
            }
        });
    }

    public static class InnerStringValue extends RuntimeValue<String> {
        private InnerStringValue(String value) {
            super(value);
        }
    }
}