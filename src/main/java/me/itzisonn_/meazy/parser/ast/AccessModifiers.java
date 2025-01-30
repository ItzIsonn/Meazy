package me.itzisonn_.meazy.parser.ast;

import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

/**
 * All basic access modifiers
 *
 * @see Registries#ACCESS_MODIFIERS
 */
public final class AccessModifiers {
    private static boolean isInit = false;

    private AccessModifiers() {}



    private static void register(String id) {
        Registries.ACCESS_MODIFIERS.register(RegistryIdentifier.ofDefault(id), id);
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
