package me.itzisonn_.meazy.parser.operator;

import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryEntry;
import me.itzisonn_.meazy.registry.RegistryIdentifier;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidValueException;
import me.itzisonn_.meazy.runtime.value.BooleanValue;
import me.itzisonn_.meazy.runtime.value.NullValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.StringValue;
import me.itzisonn_.meazy.runtime.value.number.*;

/**
 * All basic Operators
 *
 * @see Registries#OPERATORS
 */
public final class Operators {
    private static boolean isInit = false;

    private Operators() {}



    public static Operator PLUS() {
        return Registries.OPERATORS.getEntry(RegistryIdentifier.ofDefault("plus")).getValue();
    }

    public static Operator MINUS() {
        return Registries.OPERATORS.getEntry(RegistryIdentifier.ofDefault("minus")).getValue();
    }

    public static Operator MULTIPLY() {
        return Registries.OPERATORS.getEntry(RegistryIdentifier.ofDefault("multiply")).getValue();
    }

    public static Operator DIVIDE() {
        return Registries.OPERATORS.getEntry(RegistryIdentifier.ofDefault("divide")).getValue();
    }

    public static Operator PERCENT() {
        return Registries.OPERATORS.getEntry(RegistryIdentifier.ofDefault("percent")).getValue();
    }

    public static Operator POWER() {
        return Registries.OPERATORS.getEntry(RegistryIdentifier.ofDefault("power")).getValue();
    }

    public static Operator NEGATION() {
        return Registries.OPERATORS.getEntry(RegistryIdentifier.ofDefault("negation")).getValue();
    }



    public static Operator AND() {
        return Registries.OPERATORS.getEntry(RegistryIdentifier.ofDefault("and")).getValue();
    }

    public static Operator OR() {
        return Registries.OPERATORS.getEntry(RegistryIdentifier.ofDefault("or")).getValue();
    }

    public static Operator INVERSION() {
        return Registries.OPERATORS.getEntry(RegistryIdentifier.ofDefault("inversion")).getValue();
    }

    public static Operator EQUALS() {
        return Registries.OPERATORS.getEntry(RegistryIdentifier.ofDefault("equals")).getValue();
    }

    public static Operator NOT_EQUALS() {
        return Registries.OPERATORS.getEntry(RegistryIdentifier.ofDefault("not_equals")).getValue();
    }

    public static Operator GREATER() {
        return Registries.OPERATORS.getEntry(RegistryIdentifier.ofDefault("greater")).getValue();
    }

    public static Operator GREATER_OR_EQUALS() {
        return Registries.OPERATORS.getEntry(RegistryIdentifier.ofDefault("greater_or_equals")).getValue();
    }

    public static Operator LESS() {
        return Registries.OPERATORS.getEntry(RegistryIdentifier.ofDefault("less")).getValue();
    }

    public static Operator LESS_OR_EQUALS() {
        return Registries.OPERATORS.getEntry(RegistryIdentifier.ofDefault("less_or_equals")).getValue();
    }



    /**
     * Finds registered Operator with given symbol and given type
     *
     * @param symbol Operator's symbol
     * @param operatorType Operator's type or null if any
     * @return Operator with given symbol or null
     */
    public static Operator parse(String symbol, OperatorType operatorType) {
        for (RegistryEntry<Operator> entry : Registries.OPERATORS.getEntries()) {
            Operator operator = entry.getValue();
            if (symbol.equals(operator.getSymbol()) && (operatorType == null || operator.getOperatorType() == operatorType)) return operator;
        }

        return null;
    }

    /**
     * Finds registered Operator with given symbol and any type
     *
     * @param symbol Operator's symbol
     * @return Operator with given symbol or null
     */
    public static Operator parse(String symbol) {
        return parse(symbol, null);
    }

    /**
     * Finds registered Operator with given id
     *
     * @param id Operator's id
     * @return Operator with given id or null
     */
    public static Operator parseById(String id) {
        for (RegistryEntry<Operator> entry : Registries.OPERATORS.getEntries()) {
            if (entry.getIdentifier().getId().equals(id)) return entry.getValue();
        }

        return null;
    }



    private static void register(String id, Operator operator) {
        Registries.OPERATORS.register(RegistryIdentifier.ofDefault(id), operator);
    }

    /**
     * Initializes {@link Registries#OPERATORS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#OPERATORS} registry has already been initialized
     */
    public static void INIT() {
        if (isInit) throw new IllegalStateException("Operators have already been initialized!");
        isInit = true;

        register("plus", new Operator("+", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
                    return optimalNumberValue(numberValue1.getValue().doubleValue() + numberValue2.getValue().doubleValue());
                }
                return new StringValue(String.valueOf(value1.getValue()) + value2.getValue());
            }
        });
        register("minus", new Operator("-", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
                    return optimalNumberValue(numberValue1.getValue().doubleValue() - numberValue2.getValue().doubleValue());
                }
                return null;
            }
        });
        register("multiply", new Operator("*", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
                    return optimalNumberValue(numberValue1.getValue().doubleValue() * numberValue2.getValue().doubleValue());
                }

                String string;
                int amount;

                if (value1 instanceof StringValue stringValue && value2 instanceof IntValue numberValue) {
                    string = stringValue.getValue();
                    amount = numberValue.getValue();
                }
                else if (value2 instanceof StringValue stringValue && value1 instanceof IntValue numberValue) {
                    string = stringValue.getValue();
                    amount = numberValue.getValue();
                }
                else throw new InvalidSyntaxException("Can't multiply values " + value1 + " and " + value2);

                if (amount < 0) throw new InvalidSyntaxException("Can't multiply string by a negative int");

                return new StringValue(new StringBuilder().repeat(string, amount).toString());
            }
        });
        register("divide", new Operator("/", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
                    return optimalNumberValue(numberValue1.getValue().doubleValue() / numberValue2.getValue().doubleValue());
                }
                return null;
            }
        });
        register("percent", new Operator("%", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
                    return optimalNumberValue(numberValue1.getValue().doubleValue() % numberValue2.getValue().doubleValue());
                }
                return null;
            }
        });
        register("power", new Operator("^", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
                    return optimalNumberValue(Math.pow(numberValue1.getValue().doubleValue(), numberValue2.getValue().doubleValue()));
                }
                return null;
            }
        });
        register("negation", new Operator("-", OperatorType.PREFIX) {
            @Override
            public RuntimeValue<?> calculate(RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue) {
                    return optimalNumberValue(-numberValue.getValue().doubleValue());
                }
                return null;
            }
        });

        register("and", new Operator("&&", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof BooleanValue booleanValue1 && value2 instanceof BooleanValue booleanValue2) {
                    return new BooleanValue(booleanValue1.getValue() && booleanValue2.getValue());
                }
                return null;
            }
        });
        register("or", new Operator("||", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof BooleanValue booleanValue1 && value2 instanceof BooleanValue booleanValue2) {
                    return new BooleanValue(booleanValue1.getValue() || booleanValue2.getValue());
                }
                return null;
            }
        });
        register("inversion", new Operator("!", OperatorType.PREFIX) {
            @Override
            public RuntimeValue<?> calculate(RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof BooleanValue booleanValue) {
                    return new BooleanValue(!booleanValue.getValue());
                }
                return null;
            }
        });
        register("equals", new Operator("==", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NullValue) return new BooleanValue(value2 instanceof NullValue);
                if (value1.getValue() == null) return new BooleanValue(value1.equals(value2));
                return new BooleanValue(value1.getValue().equals(value2.getValue()));
            }
        });
        register("not_equals", new Operator("!=", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NullValue) return new BooleanValue(!(value2 instanceof NullValue));
                return new BooleanValue(!value1.getValue().equals(value2.getValue()));
            }
        });
        register("greater", new Operator(">", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
                    return new BooleanValue(numberValue1.getValue().doubleValue() > numberValue2.getValue().doubleValue());
                }
                if (value1 instanceof StringValue stringValue1 && value2 instanceof StringValue stringValue2) {
                    return new BooleanValue(stringValue1.getValue().length() > stringValue2.getValue().length());
                }
                return null;
            }
        });
        register("greater_or_equals", new Operator(">=", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
                    return new BooleanValue(numberValue1.getValue().doubleValue() >= numberValue2.getValue().doubleValue());
                }
                if (value1 instanceof StringValue stringValue1 && value2 instanceof StringValue stringValue2) {
                    return new BooleanValue(stringValue1.getValue().length() >= stringValue2.getValue().length());
                }
                return null;
            }
        });
        register("less", new Operator("<", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
                    return new BooleanValue(numberValue1.getValue().doubleValue() < numberValue2.getValue().doubleValue());
                }
                if (value1 instanceof StringValue stringValue1 && value2 instanceof StringValue stringValue2) {
                    return new BooleanValue(stringValue1.getValue().length() < stringValue2.getValue().length());
                }
                return null;
            }
        });
        register("less_or_equals", new Operator("<=", OperatorType.INFIX) {
            @Override
            public RuntimeValue<?> calculate(RuntimeValue<?> value1, RuntimeValue<?> value2) {
                if (value1 instanceof NumberValue<?> numberValue1 && value2 instanceof NumberValue<?> numberValue2) {
                    return new BooleanValue(numberValue1.getValue().doubleValue() <= numberValue2.getValue().doubleValue());
                }
                if (value1 instanceof StringValue stringValue1 && value2 instanceof StringValue stringValue2) {
                    return new BooleanValue(stringValue1.getValue().length() <= stringValue2.getValue().length());
                }
                return null;
            }
        });
    }

    private static NumberValue<?> optimalNumberValue(double value) {
        if (value % 1 == 0) {
            if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) return new IntValue((int) value);
            if (value >= Long.MIN_VALUE && value <= Long.MAX_VALUE) return new LongValue((long) value);
        }
        else {
            if (value >= -Float.MAX_VALUE && value <= Float.MAX_VALUE) return new FloatValue((float) value);
            if (value >= -Double.MAX_VALUE && value <= Double.MAX_VALUE) return new DoubleValue(value);
        }
        throw new InvalidValueException("Resulted value " + value + " is out of bounds");
    }
}
