package me.itzisonn_.meazy.runtime.environment.basic.default_classes;

import me.itzisonn_.meazy.parser.ast.AccessModifiers;
import me.itzisonn_.meazy.parser.ast.DataTypes;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.basic.BasicClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.values.BooleanValue;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.StringValue;
import me.itzisonn_.meazy.runtime.values.clazz.ClassValue;
import me.itzisonn_.meazy.runtime.values.function.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.number.DoubleValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TypesClassEnvironment extends BasicClassEnvironment {
    public TypesClassEnvironment(Environment parent) {
        super(parent, true, "Types");


        declareFunction(new DefaultFunctionValue("getType", new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.ANY(), true))), DataTypes.STRING(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                Object value = functionArgs.getFirst().getFinalValue();
                if (value == null) return new StringValue("null");
                if (DataTypes.BOOLEAN().isMatches(value)) return new StringValue(DataTypes.BOOLEAN().getName());
                if (DataTypes.INT().isMatches(value)) return new StringValue(DataTypes.INT().getName());
                if (DataTypes.FLOAT().isMatches(value)) return new StringValue(DataTypes.FLOAT().getName());
                if (DataTypes.STRING().isMatches(value)) return new StringValue(DataTypes.STRING().getName());
                if (functionArgs.getFirst().getFinalRuntimeValue() instanceof ClassValue classValue) return new StringValue(classValue.getId());
                return new StringValue("???");
            }
        });

                declareFunction(new DefaultFunctionValue("convert",
                new ArrayList<>(List.of(new CallArgExpression("value", DataTypes.ANY(), true), new CallArgExpression("type", DataTypes.STRING(), true))),
                DataTypes.ANY(), this, Set.of(AccessModifiers.SHARED())) {
            public RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                String type = functionArgs.get(1).getFinalValue().toString();

                Object value = functionArgs.getFirst().getFinalValue();
                if (value == null) throw new IllegalArgumentException("Can't parse value to type " + type);

                switch (type) {
                    case "any" -> {
                        return functionArgs.getFirst().getFinalRuntimeValue();
                    }
                    case "boolean" -> {
                        if (value.toString().equals("true") || value.toString().equals("false")) return new BooleanValue(Boolean.parseBoolean(value.toString()));
                        throw new IllegalArgumentException("Can't parse " + value + " to type " + type);
                    }
                    case "int" -> {
                        try {
                            return new IntValue(Integer.parseInt(value.toString().replaceAll("\\.0$", "")));
                        }
                        catch (NumberFormatException ignore) {
                            throw new IllegalArgumentException("Can't parse " + value + " to type " + type);
                        }
                    }
                    case "float" -> {
                        try {
                            return new DoubleValue(Double.parseDouble(value.toString()));
                        }
                        catch (NumberFormatException ignore) {
                            throw new IllegalArgumentException("Can't parse " + value + " to type " + type);
                        }
                    }
                    case "string" -> {
                        return new StringValue(value.toString());
                    }
                    default -> throw new IllegalArgumentException("Can only parse default types");
                }
            }
        });
    }
}
