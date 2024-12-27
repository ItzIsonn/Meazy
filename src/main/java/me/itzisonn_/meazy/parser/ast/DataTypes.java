package me.itzisonn_.meazy.parser.ast;

import me.itzisonn_.meazy.parser.ast.expression.literal.BooleanLiteral;
import me.itzisonn_.meazy.parser.ast.expression.literal.NumberLiteral;
import me.itzisonn_.meazy.parser.ast.expression.literal.StringLiteral;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryEntry;
import me.itzisonn_.meazy.registry.RegistryIdentifier;
import me.itzisonn_.meazy.runtime.values.BooleanValue;
import me.itzisonn_.meazy.runtime.values.InnerStringValue;
import me.itzisonn_.meazy.runtime.values.clazz.ClassValue;
import me.itzisonn_.meazy.runtime.values.clazz.DefaultClassValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;
import me.itzisonn_.meazy.runtime.values.number.NumberValue;

import java.util.function.Predicate;

public final class DataTypes {
    private static boolean isInit = false;

    private DataTypes() {}



    public static DataType ANY() {
        return Registries.DATA_TYPES.getEntry(RegistryIdentifier.ofDefault("any")).getValue();
    }

    public static DataType BOOLEAN() {
        return Registries.DATA_TYPES.getEntry(RegistryIdentifier.ofDefault("boolean")).getValue();
    }

    public static DataType INT() {
        return Registries.DATA_TYPES.getEntry(RegistryIdentifier.ofDefault("int")).getValue();
    }

    public static DataType FLOAT() {
        return Registries.DATA_TYPES.getEntry(RegistryIdentifier.ofDefault("float")).getValue();
    }

    public static DataType STRING() {
        return Registries.DATA_TYPES.getEntry(RegistryIdentifier.ofDefault("string")).getValue();
    }



    /**
     * Returns existing DataType or creates new that matches classes with given name
     *
     * @param dataType DataType's name
     * @return Existing or created DataType
     * @throws NullPointerException When given DataType's name is null
     * @see Registries#DATA_TYPES
     */
    public static DataType parse(String dataType) throws NullPointerException {
        if (dataType == null) throw new NullPointerException("DataType's name can't be null");

        for (RegistryEntry<DataType> entry : Registries.DATA_TYPES.getEntries()) {
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



    private static void register(String id, Predicate<Object> predicate) {
        Registries.DATA_TYPES.register(RegistryIdentifier.ofDefault(id), new DataType(id) {
            public boolean isMatches(Object value) {
                return predicate.test(value);
            }
        });
    }

    public static void INIT() {
        if (isInit) throw new IllegalStateException("DataTypes already initialized!");
        isInit = true;

        register("any", o -> true);

        register("boolean", o -> {
            if (o instanceof Boolean || o instanceof BooleanLiteral || o instanceof BooleanValue) return true;
            return o.toString().equals("true") || o.toString().equals("false");
        });

        register("int", o -> {
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

        register("float", o -> {
            if (o instanceof Float || o instanceof NumberLiteral || o instanceof NumberValue) return true;
            try {
                Float.parseFloat(o.toString());
                return true;
            }
            catch (NumberFormatException ignore) {
                return false;
            }
        });

        register("string", o -> {
            if (o instanceof DefaultClassValue defaultClassValue) return defaultClassValue.getId().equals("string");
            return o instanceof String || o instanceof StringLiteral || o instanceof InnerStringValue;
        });
    }
}
