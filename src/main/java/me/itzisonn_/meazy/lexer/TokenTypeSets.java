package me.itzisonn_.meazy.lexer;

import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

public final class TokenTypeSets {
    private static boolean isInit = false;

    private TokenTypeSets() {}



    public static TokenTypeSet KEYWORDS() {
        return Registries.TOKEN_TYPE_SETS.getEntry(RegistryIdentifier.ofDefault("keywords")).getValue();
    }

    public static TokenTypeSet OPERATOR_ASSIGN() {
        return Registries.TOKEN_TYPE_SETS.getEntry(RegistryIdentifier.ofDefault("operator_assign")).getValue();
    }

    public static TokenTypeSet OPERATOR_POSTFIX() {
        return Registries.TOKEN_TYPE_SETS.getEntry(RegistryIdentifier.ofDefault("operator_postfix")).getValue();
    }

    public static TokenTypeSet ACCESS_MODIFIERS() {
        return Registries.TOKEN_TYPE_SETS.getEntry(RegistryIdentifier.ofDefault("access_modifiers")).getValue();
    }



    private static void register(String id, TokenTypeSet tokenTypeSet) {
        Registries.TOKEN_TYPE_SETS.register(RegistryIdentifier.ofDefault(id), tokenTypeSet);
    }

    /**
     * Initializes {@link Registries#TOKEN_TYPE_SETS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#TOKEN_TYPE_SETS} registry has already been initialized
     */
    public static void INIT() {
        if (isInit) throw new IllegalStateException("TokenTypeSets have already been initialized!");
        isInit = true;

        register("keywords", new TokenTypeSet(
                TokenTypes.VARIABLE(),
                TokenTypes.FUNCTION(),
                TokenTypes.CLASS(),
                TokenTypes.CONSTRUCTOR(),
                TokenTypes.NEW(),
                TokenTypes.PRIVATE(),
                TokenTypes.SHARED(),
                TokenTypes.IF(),
                TokenTypes.ELSE(),
                TokenTypes.FOR(),
                TokenTypes.IN(),
                TokenTypes.WHILE(),
                TokenTypes.RETURN(),
                TokenTypes.CONTINUE(),
                TokenTypes.BREAK(),
                TokenTypes.NULL(),
                TokenTypes.BOOLEAN()));

        register("operator_assign", new TokenTypeSet(
                TokenTypes.PLUS_ASSIGN(),
                TokenTypes.MINUS_ASSIGN(),
                TokenTypes.MULTIPLY_ASSIGN(),
                TokenTypes.DIVIDE_ASSIGN(),
                TokenTypes.PERCENT_ASSIGN(),
                TokenTypes.POWER_ASSIGN()));

        register("operator_postfix", new TokenTypeSet(
                TokenTypes.DOUBLE_PLUS(),
                TokenTypes.DOUBLE_MINUS()));

        register("access_modifiers", new TokenTypeSet(
                TokenTypes.PRIVATE(),
                TokenTypes.SHARED()));
    }
}
