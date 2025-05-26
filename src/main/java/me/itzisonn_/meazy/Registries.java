package me.itzisonn_.meazy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.addon.addon_info.AddonInfo;
import me.itzisonn_.meazy.command.Command;
import me.itzisonn_.meazy.command.Commands;
import me.itzisonn_.meazy.lexer.*;
import me.itzisonn_.meazy.parser.data_type.DataTypeFactory;
import me.itzisonn_.meazy.parser.json_converter.*;
import me.itzisonn_.meazy.parser.json_converter.basic.CallArgExpressionConverter;
import me.itzisonn_.meazy.parser.json_converter.basic.ExpressionConverter;
import me.itzisonn_.meazy.parser.json_converter.basic.ProgramConverter;
import me.itzisonn_.meazy.parser.json_converter.basic.StatementConverter;
import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ParsingFunction;
import me.itzisonn_.meazy.runtime.environment.factory.*;
import me.itzisonn_.meazy.version.Version;
import me.itzisonn_.registry.RegistryEntry;
import me.itzisonn_.registry.RegistryIdentifier;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.Program;
import me.itzisonn_.registry.multiple_entry.Pair;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.registry.multiple_entry.OrderedRegistry;
import me.itzisonn_.registry.multiple_entry.PairRegistry;
import me.itzisonn_.registry.multiple_entry.SetRegistry;
import me.itzisonn_.registry.single_entry.SingleEntryRegistry;
import me.itzisonn_.registry.single_entry.SingleEntryRegistryImpl;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.interpreter.*;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;

/**
 * All basic Registries
 */
public final class Registries {
    private static boolean isInit = false;

    private Registries() {}



    /**
     * Registry for all Commands
     *
     * @see Command
     */
    public static final SetRegistry<Command> COMMANDS = new SetRegistry<>() {
        @Override
        public void register(RegistryIdentifier identifier, Command value, boolean overridable) {
            for (RegistryEntry<Command> entry : Registries.COMMANDS.getEntries()) {
                if (entry.getValue().getName().equals(value.getName()) && !entry.isOverrideable()) {
                    throw new IllegalArgumentException("Command with name " + value.getName() + " has already been registered");
                }
            }

            super.register(identifier, value, overridable);
        }
    };



    /**
     * Registry for all TokenTypes
     */
    public static final SetRegistry<TokenType> TOKEN_TYPES = new SetRegistry<>();

    /**
     * Registry for all TokenTypesSets
     */
    public static final SetRegistry<TokenTypeSet> TOKEN_TYPE_SETS = new SetRegistry<>();

    /**
     * Registry for tokenization function that is used to tokenize given string
     *
     * @see Token
     * @see Registries#TOKEN_TYPES
     */
    public static final SingleEntryRegistry<Function<String, List<Token>>> TOKENIZATION_FUNCTION = new SingleEntryRegistryImpl<>();



    /**
     * Registry for all Modifiers
     */
    public static final SetRegistry<Modifier> MODIFIERS = new SetRegistry<>();

    /**
     * Registry for all Operators
     */
    public static final SetRegistry<Operator> OPERATORS = new SetRegistry<>();

    /**
     * Registry for all ParsingFunctions
     *
     * @see ParsingFunction
     * @see Parser
     */
    public static final OrderedRegistry<ParsingFunction<? extends Statement>> PARSING_FUNCTIONS = new OrderedRegistry<>();

    /**
     * Registry for function that uses {@link Registries#PARSING_FUNCTIONS} to parse tokens into {@link Program}
     *
     * @see ParsingFunction
     * @see Parser
     */
    public static final SingleEntryRegistry<BiFunction<File, List<Token>, Program>> PARSE_TOKENS_FUNCTION = new SingleEntryRegistryImpl<>();

    /**
     * Registry for {@link DataTypeFactory}
     */
    public static final SingleEntryRegistry<DataTypeFactory> DATA_TYPE_FACTORY = new SingleEntryRegistryImpl<>();



    /**
     * Registry for all Converters that is used to compile and decompile Statements
     *
     * @see Converter
     * @see Statement
     */
    public static final PairRegistry<Class<? extends Statement>, Converter<? extends Statement>> CONVERTERS = new PairRegistry<>();

    /**
     * Returns Gson with all registered converters
     *
     * @see Registries#CONVERTERS
     */
    @Getter
    private static Gson gson = null;

    /**
     * Updates {@link Registries#gson}
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



    /**
     * Registry for EvaluationFunctions
     *
     * @see EvaluationFunction
     * @see Interpreter
     */
    public static final PairRegistry<Class<? extends Statement>, EvaluationFunction<? extends Statement>> EVALUATION_FUNCTIONS = new PairRegistry<>();

    /**
     * Registry for function that uses {@link Registries#EVALUATION_FUNCTIONS} to evaluate {@link Program}
     *
     * @see EvaluationFunction
     * @see Interpreter
     */
    public static final SingleEntryRegistry<Function<Program, GlobalEnvironment>> EVALUATE_PROGRAM_FUNCTION = new SingleEntryRegistryImpl<>();



    /**
     * Registry for {@link GlobalEnvironmentFactory}
     */
    public static final SingleEntryRegistry<GlobalEnvironmentFactory> GLOBAL_ENVIRONMENT_FACTORY = new SingleEntryRegistryImpl<>();

    /**
     * Registry for {@link ClassEnvironmentFactory}
     */
    public static final SingleEntryRegistry<ClassEnvironmentFactory> CLASS_ENVIRONMENT_FACTORY = new SingleEntryRegistryImpl<>();

    /**
     * Registry for {@link FunctionEnvironmentFactory}
     */
    public static final SingleEntryRegistry<FunctionEnvironmentFactory> FUNCTION_ENVIRONMENT_FACTORY = new SingleEntryRegistryImpl<>();

    /**
     * Registry for {@link ConstructorEnvironmentFactory}
     */
    public static final SingleEntryRegistry<ConstructorEnvironmentFactory> CONSTRUCTOR_ENVIRONMENT_FACTORY = new SingleEntryRegistryImpl<>();

    /**
     * Registry for {@link LoopEnvironmentFactory}
     */
    public static final SingleEntryRegistry<LoopEnvironmentFactory> LOOP_ENVIRONMENT_FACTORY = new SingleEntryRegistryImpl<>();

    /**
     * Registry for {@link EnvironmentFactory}
     */
    public static final SingleEntryRegistry<EnvironmentFactory> ENVIRONMENT_FACTORY = new SingleEntryRegistryImpl<>();

    /**
     * Set of native related {@link GlobalEnvironment}s
     */
    public static final Set<GlobalEnvironment> NATIVE_RELATED_GLOBAL_ENVIRONMENTS = new HashSet<>();



    /**
     * Initializes all registries
     * <p>
     * <i>Don't use this method because it's called once at Meazy initialization</i>
     *
     * @throws IllegalStateException If registries have already been initialized
     */
    public static void INIT() throws IllegalStateException {
        if (isInit) throw new IllegalStateException("Registries have already been initialized");
        isInit = true;

        Commands.INIT();
        TokenTypes.INIT();

        registerConverter(new StatementConverter());
        registerConverter(new ExpressionConverter());
        registerConverter(new ProgramConverter());
        registerConverter(new CallArgExpressionConverter());

        TOKENIZATION_FUNCTION.register(getDefaultIdentifier("tokens_function"), lines -> {
            List<Token> tokens = new ArrayList<>();
            int lineNumber = 1;

            for (int i = 0; i < lines.length(); i++) {
                String string = lines.substring(i);
                Token token = null;
                for (RegistryEntry<TokenType> entry : Registries.TOKEN_TYPES.getEntries()) {
                    TokenType tokenType = entry.getValue();
                    if (tokenType.getPattern() == null) continue;

                    Matcher matcher = tokenType.getPattern().matcher(string);
                    if (matcher.find()) {
                        int end = matcher.end();
                        String matched = string.substring(0, end);
                        if (!tokenType.canMatch(matched)) continue;

                        if (token == null || token.getValue().length() < matched.length()) {
                            token = new Token(lineNumber, tokenType, matched);
                        }
                    }
                }

                if (token == null) {
                    String errorString = string.split("\n")[0];
                    if (errorString.length() > 20) errorString = errorString.substring(0, 20) + "...";

                    throw new UnknownTokenException("At line " + lineNumber + ": " + errorString);
                }

                i += token.getValue().length() - 1;
                if (!token.getType().isShouldSkip()) tokens.add(token);

                lineNumber += (token.getValue().length() - token.getValue().replace("\n", "").length());
            }

            tokens.add(new Token(lineNumber, TokenTypes.END_OF_FILE(), ""));
            return tokens;
        });

        PARSE_TOKENS_FUNCTION.register(getDefaultIdentifier("parse_tokens"), (file, tokens) -> {
            if (tokens == null) throw new NullPointerException("Tokens can't be null");
            Parser.setTokens(tokens);

            Parser.moveOverOptionalNewLines();

            Map<String, Version> requiredAddons = new HashMap<>();
            for (Addon addon : MeazyMain.ADDON_MANAGER.getAddons()) {
                AddonInfo addonInfo = addon.getAddonInfo();
                requiredAddons.put(addonInfo.getId(), addonInfo.getVersion());
            }

            List<Statement> body = new ArrayList<>();
            while (!Parser.getCurrent().getType().equals(TokenTypes.END_OF_FILE())) {
                body.add(Parser.parse(getDefaultIdentifier("global_statement"), Statement.class));
                Parser.moveOverOptionalNewLines();
            }

            return new Program(file, MeazyMain.VERSION, requiredAddons, body);
        });
        EVALUATE_PROGRAM_FUNCTION.register(getDefaultIdentifier("evaluate_program"), program -> {
            if (program.getFile() == null) throw new NullPointerException("Program's file is null");

            for (String addonId : program.getRequiredAddons().keySet()) {
                Addon addon = MeazyMain.ADDON_MANAGER.getAddon(addonId);
                if (addon == null) throw new RuntimeException("Can't find required addon with id " + addonId);

                Version addonVersion = program.getRequiredAddons().get(addonId);
                if (addonVersion != null && !addon.getAddonInfo().getVersion().equals(addonVersion)) {
                    throw new RuntimeException("Can't find required addon with id " + addonId + " of version " + addonVersion +
                            " (found version " + addon.getAddonInfo().getVersion() + ")");
                }
            }

            GlobalEnvironment globalEnvironment = Registries.GLOBAL_ENVIRONMENT_FACTORY.getEntry().getValue().create(program.getFile());
            Interpreter.evaluate(program, globalEnvironment);

            return globalEnvironment;
        });
    }

    @SuppressWarnings("unchecked")
    private static  <T extends Statement> void registerConverter(Converter<T> converter) {
        CONVERTERS.register(
                converter.getId(),
                (Class<T>) ((ParameterizedType) converter.getClass().getGenericSuperclass()).getActualTypeArguments()[0],
                converter);
    }

    /**
     * Creates new RegistryIdentifier with 'meazy' namespace
     *
     * @param id Identifier's id that matches {@link RegistryIdentifier#IDENTIFIER_REGEX}
     * @return New RegistryIdentifier
     *
     * @apiNote Recommended to use {@link RegistryIdentifier#of(String, String)} or {@link RegistryIdentifier#of(String)}
     *          because 'meazy' namespace belongs to core identifiers
     *
     * @throws NullPointerException If id is null
     * @throws IllegalArgumentException If id doesn't match {@link RegistryIdentifier#IDENTIFIER_REGEX}
     */
    public static RegistryIdentifier getDefaultIdentifier(String id) throws NullPointerException, IllegalArgumentException {
        return RegistryIdentifier.of("meazy", id);
    }
}