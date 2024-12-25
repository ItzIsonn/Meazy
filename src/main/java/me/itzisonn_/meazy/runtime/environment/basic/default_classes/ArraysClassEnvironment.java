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


        declareFunction(new DefaultFunctionValue("size", new ArrayList<>(List.of(new CallArgExpression("array", new NullLiteral(), DataTypes.ANY(), true))), DataTypes.INT(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionArgs.getFirst().getFinalValue();
                if (value instanceof ArrayList<?> arrayList) {
                    return new IntValue(arrayList.size());
                }
                throw new InvalidSyntaxException("Can't get size of non-array value");
            }
        });

        declareFunction(new DefaultFunctionValue("fill",
                new ArrayList<>(List.of(new CallArgExpression("array", new NullLiteral(), DataTypes.ANY(), true), new CallArgExpression("value", DataTypes.ANY(), true))),
                null, this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object array = functionArgs.getFirst().getFinalValue();
                Object value = functionArgs.get(1).getFinalRuntimeValue();

                if (array instanceof ArrayList<?> arrayList) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        ((ArrayList<Object>) arrayList).set(i, value);
                    }
                    return null;
                }
                throw new InvalidSyntaxException("Can't fill non-array value");
            }
        });

        declareFunction(new DefaultFunctionValue("copy", new ArrayList<>(List.of(new CallArgExpression("array", new NullLiteral(), DataTypes.ANY(), true))), DataTypes.ANY(), new NullValue(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionArgs.getFirst().getFinalValue();
                if (value instanceof ArrayList<?> arrayList) {
                    return new ArrayValue(new ArrayList<>((ArrayList<RuntimeValue<?>>) arrayList));
                }
                throw new InvalidSyntaxException("Can't copy non-array value");
            }
        });

        declareFunction(new DefaultFunctionValue("toString", new ArrayList<>(List.of(new CallArgExpression("array", new NullLiteral(), DataTypes.ANY(), true))), DataTypes.STRING(), null, this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionArgs.getFirst().getFinalValue();
                if (value instanceof ArrayList<?> arrayList) {
                    StringBuilder toReturn = new StringBuilder();
                    for (int i = 0; i < arrayList.size(); i++) {
                        Object object = arrayList.get(i);
                        if (object instanceof RuntimeValue<?> runtimeValue) {
                            toReturn.append(runtimeValue.getFinalValue());
                        }
                        else toReturn.append(object);
                        if (i < arrayList.size() - 1) toReturn.append(", ");
                    }
                    return new StringValue(toReturn.toString());
                }
                throw new InvalidSyntaxException("Can't get string value of non-array value");
            }
        });
    }
}
