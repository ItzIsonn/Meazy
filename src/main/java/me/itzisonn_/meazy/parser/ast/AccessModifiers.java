package me.itzisonn_.meazy.parser.ast;

import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryEntry;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

public final class AccessModifiers {
    private static boolean isInit = false;

    private AccessModifiers() {}



    public static AccessModifier PRIVATE() {
        return Registries.ACCESS_MODIFIER.getEntry(RegistryIdentifier.ofDefault("private")).getValue();
    }

    public static AccessModifier SHARED() {
        return Registries.ACCESS_MODIFIER.getEntry(RegistryIdentifier.ofDefault("shared")).getValue();
    }



    /**
     * Returns existing AccessModifier with given name
     *
     * @param accessModifier AccessModifier's name
     * @return Existing AccessModifier
     * @throws NullPointerException When given AccessModifier's name is null
     * @throws IllegalArgumentException When no existing AccessModifiers doesn't have given AccessModifier's name
     */
    public static AccessModifier parse(String accessModifier) throws NullPointerException {
        if (accessModifier == null) throw new NullPointerException("AccessModifier's name can't be null");

        RegistryEntry<AccessModifier> entry = Registries.ACCESS_MODIFIER.getEntry(RegistryIdentifier.ofDefault(accessModifier));
        if (entry == null) throw new IllegalArgumentException("Unknown AccessModifier with name " + accessModifier);

        return entry.getValue();
    }



    private static void register(String id) {
        Registries.ACCESS_MODIFIER.register(RegistryIdentifier.ofDefault(id), new AccessModifier(id));
    }

    public static void INIT() {
        if (isInit) throw new IllegalStateException("AccessModifiers already initialized!");
        isInit = true;

        register("private");
        register("shared");
    }
}
