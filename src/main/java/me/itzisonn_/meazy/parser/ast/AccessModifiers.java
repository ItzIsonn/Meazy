package me.itzisonn_.meazy.parser.ast;

import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryEntry;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

/**
 * All basic AccessModifiers
 *
 * @see Registries#ACCESS_MODIFIERS
 */
public final class AccessModifiers {
    private static boolean isInit = false;

    private AccessModifiers() {}



    public static AccessModifier PRIVATE() {
        return Registries.ACCESS_MODIFIERS.getEntry(RegistryIdentifier.ofDefault("private")).getValue();
    }

    public static AccessModifier SHARED() {
        return Registries.ACCESS_MODIFIERS.getEntry(RegistryIdentifier.ofDefault("shared")).getValue();
    }



    /**
     * Returns existing AccessModifier with given name
     *
     * @param name AccessModifier's name
     * @return Existing AccessModifier
     *
     * @throws NullPointerException When given name is null
     * @throws IllegalArgumentException When no existing AccessModifiers have given name
     *
     * @see Registries#ACCESS_MODIFIERS
     */
    public static AccessModifier parse(String name) throws NullPointerException, IllegalArgumentException {
        if (name == null) throw new NullPointerException("AccessModifier's name can't be null");

        RegistryEntry<AccessModifier> entry = Registries.ACCESS_MODIFIERS.getEntry(RegistryIdentifier.ofDefault(name));
        if (entry == null) throw new IllegalArgumentException("Unknown AccessModifier with name " + name);

        return entry.getValue();
    }



    private static void register(String id) {
        Registries.ACCESS_MODIFIERS.register(RegistryIdentifier.ofDefault(id), new AccessModifier(id));
    }

    /**
     * Initializes {@link Registries#ACCESS_MODIFIERS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#ACCESS_MODIFIERS} registry has already been initialized
     */
    public static void INIT() {
        if (isInit) throw new IllegalStateException("AccessModifiers have already been initialized!");
        isInit = true;

        register("private");
        register("shared");
    }
}
