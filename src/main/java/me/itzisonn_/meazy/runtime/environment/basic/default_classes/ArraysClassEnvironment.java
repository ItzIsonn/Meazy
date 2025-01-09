package me.itzisonn_.meazy.runtime.environment.basic.default_classes;

import me.itzisonn_.meazy.parser.ast.AccessModifiers;
import me.itzisonn_.meazy.parser.ast.DataTypes;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.parser.ast.expression.literal.NullLiteral;
import me.itzisonn_.meazy.runtime.environment.basic.BasicClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.ArrayValue;
import me.itzisonn_.meazy.runtime.values.NullValue;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.StringValue;
import me.itzisonn_.meazy.runtime.values.function.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ArraysClassEnvironment extends BasicClassEnvironment {
    @SuppressWarnings("unchecked")
    public ArraysClassEnvironment(Environment parent) {
        super(parent, true, "Arrays");


        declareFunction(new DefaultFunctionValue("getSize", new ArrayList<>(List.of(new CallArgExpression("array", new NullLiteral(), DataTypes.ANY(), true))), DataTypes.INT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionArgs.getFirst().getFinalValue();
                if (value instanceof List<?> list) {
                    return new IntValue(list.size());
                }
                throw new InvalidSyntaxException("Can't get size of non-array value");
            }
        });

        declareFunction(new DefaultFunctionValue("fill",
                new ArrayList<>(List.of(new CallArgExpression("array", new NullLiteral(), DataTypes.ANY(), true), new CallArgExpression("value", DataTypes.ANY(), true))),
                null, this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object array = functionArgs.getFirst().getFinalValue();
                Object value = functionArgs.get(1).getFinalRuntimeValue();

                if (array instanceof List<?> list) {
                    for (int i = 0; i < list.size(); i++) {
                        ((List<Object>) list).set(i, value);
                    }
                    return null;
                }
                throw new InvalidSyntaxException("Can't fill non-array value");
            }
        });

        declareFunction(new DefaultFunctionValue("copy", new ArrayList<>(List.of(new CallArgExpression("array", new NullLiteral(), DataTypes.ANY(), true))), DataTypes.ANY(), new NullValue(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionArgs.getFirst().getFinalValue();
                if (value instanceof List<?> list) {
                    return new ArrayValue(new ArrayList<>((List<RuntimeValue<?>>) list));
                }
                throw new InvalidSyntaxException("Can't copy non-array value");
            }
        });

        declareFunction(new DefaultFunctionValue("toString", new ArrayList<>(List.of(new CallArgExpression("array", new NullLiteral(), DataTypes.ANY(), true))), DataTypes.STRING(), null, this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionArgs.getFirst().getFinalValue();
                if (value instanceof List<?> list) {
                    StringBuilder toReturn = new StringBuilder();
                    toReturn.append("[");
                    for (int i = 0; i < list.size(); i++) {
                        Object object = list.get(i);
                        if (object instanceof RuntimeValue<?> runtimeValue) {
                            toReturn.append(runtimeValue.getFinalValue());
                        }
                        else toReturn.append(object);
                        if (i < list.size() - 1) toReturn.append(", ");
                    }
                    toReturn.append("]");
                    return new StringValue(toReturn.toString());
                }
                throw new InvalidSyntaxException("Can't get string value of non-array value");
            }
        });
    }
}
