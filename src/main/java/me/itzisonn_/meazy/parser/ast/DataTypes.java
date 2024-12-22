package me.itzisonn_.meazy.parser.ast;

import me.itzisonn_.meazy.parser.ast.expression.literal.BooleanLiteral;
import me.itzisonn_.meazy.parser.ast.expression.literal.NumberLiteral;
import me.itzisonn_.meazy.parser.ast.expression.literal.StringLiteral;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryEntry;
import me.itzisonn_.meazy.registry.RegistryIdentifier;
import me.itzisonn_.meazy.runtime.values.BooleanValue;
import me.itzisonn_.meazy.runtime.values.StringValue;
import me.itzisonn_.meazy.runtime.values.clazz.ClassValue;
import me.itzisonn_.meazy.runtime.values.clazz.DefaultClassValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;
import me.itzisonn_.meazy.runtime.values.number.NumberValue;

import java.util.function.Predicate;

public class DataTypes {
    public static final DataType ANY = register("any", "any", o -> true);

    public static final DataType BOOLEAN = register("boolean", "boolean", o -> {
        if (o instanceof Boolean || o instanceof BooleanLiteral || o instanceof BooleanValue) return true;
        return o.toString().equals("true") || o.toString().equals("false");
    });

    public static final DataType INT = register("int", "int", o -> {
        if (o instanceof Integer || o instanceof IntValue) return true;
        if (o instanceof NumberLiteral numberLiteral) {
            return numberLiteral.isInt();
        }
        try {
            Integer.parseInt(o.toString());
            return true;
        }
        catch (NumberFormatException ignore) {
            return false;
        }
    });

    public static final DataType FLOAT = register("float", "float", o -> {
        if (o instanceof Float || o instanceof NumberLiteral || o instanceof NumberValue) return true;
        try {
            Float.parseFloat(o.toString());
            return true;
        }
        catch (NumberFormatException ignore) {
            return false;
        }
    });

    public static final DataType STRING = register("string", "string", o ->
            o instanceof String || o instanceof StringLiteral || o instanceof StringValue);



    /**
     * Returns existing DataType or creates new that matches classes with given name
     *
     * @param dataType DataType's name
     * @return Existing or created DataType
     * @throws NullPointerException When given DataType's name is null
     */
    public static DataType parse(String dataType) throws NullPointerException {
        if (dataType == null) throw new NullPointerException("DataType's name can't be null");

        for (RegistryEntry<DataType> entry : Registries.DATA_TYPE.getEntries()) {
            if (entry.getValue().getName().equals(dataType)) {
                return entry.getValue();
            }
        }

        return new DataType(dataType) {
            public boolean isMatches(Object value) {
                if (value instanceof ClassValue classValue) return classValue.getId().equals(dataType);
                if (value instanceof DefaultClassValue defaultClassValue) return defaultClassValue.getId().equals(dataType);
                return false;
            }
        };
    }

    private static DataType register(String id, String name, Predicate<Object> predicate) {
        return Registries.DATA_TYPE.register(RegistryIdentifier.ofDefault(id), new DataType(name) {
            public boolean isMatches(Object value) {
                return predicate.test(value);
            }
        });
    }

    public static void INIT() {}
}
