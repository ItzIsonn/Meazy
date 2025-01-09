package me.itzisonn_.meazy.parser.json_converters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.statement.*;
import me.itzisonn_.meazy.parser.json_converters.expression.*;
import me.itzisonn_.meazy.parser.json_converters.expression.call_expression.*;
import me.itzisonn_.meazy.parser.json_converters.expression.identifier.*;
import me.itzisonn_.meazy.parser.json_converters.expression.literal.*;
import me.itzisonn_.meazy.parser.json_converters.statement.*;
import me.itzisonn_.meazy.registry.multiple_entry.Pair;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryEntry;

import java.lang.reflect.ParameterizedType;

/**
 * All basic Converters
 *
 * @see Registries#CONVERTERS
 */
public final class Converters {
    private static boolean isInit = false;

    /**
     * Returns Gson with all registered converters
     *
     * @see Registries#CONVERTERS
     */
    @Getter
    private static Gson gson = null;

    private Converters() {}



    /**
     * Updates Gson
     *
     * @see Registries#CONVERTERS
     */
    public static void updateGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        for (RegistryEntry<Pair<Class<? extends Statement>, Converter<? extends Statement>>> entry : Registries.CONVERTERS.getEntries()) {
            gsonBuilder.registerTypeAdapter(entry.getValue().getKey(), entry.getValue().getValue());
        }
        gson = gsonBuilder.create();
    }



    @SuppressWarnings("unchecked")
    private static <T extends Statement> void register(Converter<T> converter) {
        Registries.CONVERTERS.register(
                converter.getIdentifier(),
                (Class<T>) ((ParameterizedType) converter.getClass().getGenericSuperclass()).getActualTypeArguments()[0],
                converter);
    }

    /**
     * Initializes {@link Registries#CONVERTERS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#CONVERTERS} registry has already been initialized
     */
    public static void INIT() {
        if (isInit) throw new IllegalStateException("Converters have already been initialized!");
        isInit = true;

        register(new BreakStatementConverter());
        register(new ClassDeclarationStatementConverter());
        register(new ConstructorDeclarationStatementConverter());
        register(new ContinueStatementConverter());
        register(new ForeachStatementConverter());
        register(new ForStatementConverter());
        register(new FunctionDeclarationStatementConverter());
        register(new IfStatementConverter());
        register(new ProgramConverter());
        register(new ReturnStatementConverter());
        register(new VariableDeclarationConverter());
        register(new WhileStatementConverter());

        register(new BooleanLiteralConverter());
        register(new NullLiteralConverter());
        register(new NumberLiteralConverter());
        register(new StringLiteralConverter());

        register(new ArrayDeclarationExpressionConverter());
        register(new ArrayPointerExpressionConverter());
        register(new AssignmentExpressionConverter());
        register(new BinaryExpressionConverter());
        register(new CallArgExpressionConverter());
        register(new ComparisonExpressionConverter());
        register(new LogicalExpressionConverter());
        register(new MemberExpressionConverter());
        register(new ClassCallExpressionConverter());
        register(new FunctionCallExpressionConverter());
        register(new ClassIdentifierConverter());
        register(new FunctionIdentifierConverter());
        register(new VariableIdentifierConverter());

        register(new StatementConverter());
        register(new ExpressionConverter());

        updateGson();
    }
}
