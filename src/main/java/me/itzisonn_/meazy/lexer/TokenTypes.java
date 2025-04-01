package me.itzisonn_.meazy.lexer;

import me.itzisonn_.meazy.Registries;

import java.util.regex.Pattern;

/**
 * All basic TokenTypes
 *
 * @see Registries#TOKEN_TYPES
 */
public final class TokenTypes {
    private static boolean isInit = false;

    private TokenTypes() {}



    public static TokenType NEW_LINE() {
        return Registries.TOKEN_TYPES.getEntry(Registries.getDefaultIdentifier("new_line")).getValue();
    }

    public static TokenType WHITE_SPACE() {
        return Registries.TOKEN_TYPES.getEntry(Registries.getDefaultIdentifier("white_space")).getValue();
    }

    public static TokenType END_OF_FILE() {
        return Registries.TOKEN_TYPES.getEntry(Registries.getDefaultIdentifier("end_of_file")).getValue();
    }



    private static void register(TokenType tokenType) {
        Registries.TOKEN_TYPES.register(Registries.getDefaultIdentifier(tokenType.getId()), tokenType);
    }

    /**
     * Initializes {@link Registries#TOKEN_TYPES} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#TOKEN_TYPES} registry has already been initialized
     */
    public static void INIT() {
        if (isInit) throw new IllegalStateException("TokenTypes have already been initialized!");
        isInit = true;

        register(new TokenType("new_line", "\n+", false));
        register(new TokenType("white_space", "(?!\n)\\s", true));
        register(new TokenType("end_of_file", (Pattern) null, false));
    }
}
