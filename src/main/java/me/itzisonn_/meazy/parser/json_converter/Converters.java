package me.itzisonn_.meazy.parser.json_converter;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.json_converter.basic.CallArgExpressionConverter;
import me.itzisonn_.meazy.parser.json_converter.basic.ExpressionConverter;
import me.itzisonn_.meazy.parser.json_converter.basic.ProgramConverter;
import me.itzisonn_.meazy.parser.json_converter.basic.StatementConverter;

import java.lang.reflect.ParameterizedType;

/**
 * Converters registrar
 *
 * @see Registries#TOKEN_TYPES
 */
public final class Converters {
    private static boolean hasRegistered = false;

    private Converters() {}



    /**
     * Initializes {@link Registries#CONVERTERS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#CONVERTERS} registry has already been initialized
     */
    public static void REGISTER() {
        if (hasRegistered) throw new IllegalStateException("Converters have already been initialized");
        hasRegistered = true;

        register(new StatementConverter());
        register(new ExpressionConverter());
        register(new ProgramConverter());
        register(new CallArgExpressionConverter());
    }



    @SuppressWarnings("unchecked")
    private static  <T extends Statement> void register(Converter<T> converter) {
        Registries.CONVERTERS.register(
                converter.getId(),
                (Class<T>) ((ParameterizedType) converter.getClass().getGenericSuperclass()).getActualTypeArguments()[0],
                converter);
    }
}
