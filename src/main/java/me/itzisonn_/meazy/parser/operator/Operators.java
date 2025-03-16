package me.itzisonn_.meazy.parser.operator;

import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryEntry;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

/**
 * All basic Operators
 *
 * @see Registries#OPERATORS
 */
public final class Operators {
    private static boolean isInit = false;

    private Operators() {}



    /**
     * Finds registered Operator with given operator
     *
     * @param operator Operator of Operator
     * @return Operator with given operator or null
     */
    public static Operator parse(String operator) {
        for (RegistryEntry<Operator> entry : Registries.OPERATORS.getEntries()) {
            if (operator.equals(entry.getValue().getOperator())) return entry.getValue();
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

        register("plus", new Operator("+", true));
        register("minus", new Operator("-", true));
        register("multiply", new Operator("*", true));
        register("divide", new Operator("/", true));
        register("percent", new Operator("%", true));
        register("power", new Operator("^", true));

        register("and", new Operator("&&", true));
        register("or", new Operator("||", true));
        register("inversion", new Operator("!", false));
        register("equals", new Operator("==", true));
        register("not_equals", new Operator("!=", true));
        register("greater", new Operator(">", true));
        register("greater_or_equals", new Operator(">=", true));
        register("less", new Operator("<", true));
        register("less_or_equals", new Operator("<=", true));
    }
}
