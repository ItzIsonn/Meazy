package me.itzisonn_.meazy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.addon.AddonInfo;
import me.itzisonn_.meazy.command.Command;
import me.itzisonn_.meazy.command.Commands;
import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.lang.Language;
import me.itzisonn_.meazy.lexer.*;
import me.itzisonn_.meazy.parser.*;
import me.itzisonn_.meazy.parser.data_type.DataTypeFactory;
import me.itzisonn_.meazy.parser.json_converter.*;
import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.meazy.registry.CommandRegistry;
import me.itzisonn_.meazy.registry.LanguageRegistry;
import me.itzisonn_.meazy.runtime.EvaluateProgramFunction;
import me.itzisonn_.meazy.runtime.RunProgramFunction;
import me.itzisonn_.meazy.runtime.environment.factory.*;
import me.itzisonn_.meazy.version.Version;
import me.itzisonn_.registry.RegistryEntry;
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

import java.util.*;
import java.util.regex.Matcher;

/**
 * All basic Registries
 */
public final class Registries {
    private static boolean isInit = false;

    private Registries() {}



    /**
     * Registry for all Languages
     *
     * @see Language
     */
    public static final LanguageRegistry LANGUAGES = new LanguageRegistry();



    /**
     * Registry for all Commands
     *
     * @see Command
     */
    public static final CommandRegistry COMMANDS = new CommandRegistry();



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
    public static final SingleEntryRegistry<TokenizationFunction> TOKENIZATION_FUNCTION = new SingleEntryRegistryImpl<>();



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
    public static final OrderedRegistry<ParsingFunction<?>> PARSING_FUNCTIONS = new OrderedRegistry<>();

    /**
     * Registry for function that uses {@link Registries#PARSING_FUNCTIONS} to parse tokens into {@link Program}
     *
     * @see ParsingFunction
     * @see Parser
     */
    public static final SingleEntryRegistry<ParseTokensFunction> PARSE_TOKENS_FUNCTION = new SingleEntryRegistryImpl<>();

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
        for (RegistryEntry<Pair<Class<? extends Statement>, Converter<? extends Statement>>> entry : CONVERTERS.getEntries()) {
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
     * @see EvaluateProgramFunction
     */
    public static final SingleEntryRegistry<EvaluateProgramFunction> EVALUATE_PROGRAM_FUNCTION = new SingleEntryRegistryImpl<>();

    /**
     * Registry for function that uses {@link Registries#EVALUATE_PROGRAM_FUNCTION} to run {@link Program}
     * @see RunProgramFunction
     */
    public static final SingleEntryRegistry<RunProgramFunction> RUN_PROGRAM_FUNCTION = new SingleEntryRegistryImpl<>();



    /**
     * Registry for {@link GlobalEnvironmentFactory}
     */
    public static final SingleEntryRegistry<GlobalEnvironmentFactory> GLOBAL_ENVIRONMENT_FACTORY = new SingleEntryRegistryImpl<>();
    /**
     * Registry for {@link FileEnvironmentFactory}
     */
    public static final SingleEntryRegistry<FileEnvironmentFactory> FILE_ENVIRONMENT_FACTORY = new SingleEntryRegistryImpl<>();

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
     * Initializes Registries
     * <p>
     * <i>Don't use this method because it's called once at {@link MeazyMain} initialization</i>
     *
     * @throws IllegalStateException If Registries has already been initialized
     */
    public static void INIT() {
        if (isInit) throw new IllegalStateException("Registries have already been initialized");
        isInit = true;

        LANGUAGES.register(MeazyMain.getDefaultIdentifier("english"), new Language("en", "English"));
        LANGUAGES.register(MeazyMain.getDefaultIdentifier("russian"), new Language("ru", "Русский"));

        Commands.REGISTER();
        TokenTypes.REGISTER();
        Converters.REGISTER();

        TOKENIZATION_FUNCTION.register(MeazyMain.getDefaultIdentifier("tokens_function"), lines -> {
            List<Token> tokens = new ArrayList<>();
            int lineNumber = 1;

            for (int i = 0; i < lines.length(); i++) {
                String string = lines.substring(i);
                Token token = null;
                for (RegistryEntry<TokenType> entry : TOKEN_TYPES.getEntries()) {
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

        PARSE_TOKENS_FUNCTION.register(MeazyMain.getDefaultIdentifier("parse_tokens"), (file, tokens) -> {
            if (tokens == null) throw new NullPointerException("Tokens can't be null");
            ParsingContext parsingContext = new ParsingContext(tokens);

            Parser parser = parsingContext.getParser();
            parser.moveOverOptionalNewLines();

            Map<String, Version> requiredAddons = new HashMap<>();
            for (Addon addon : MeazyMain.ADDON_MANAGER.getAddons()) {
                AddonInfo addonInfo = addon.getAddonInfo();
                requiredAddons.put(addonInfo.getId(), addonInfo.getVersion());
            }

            List<Statement> body = new ArrayList<>();
            while (!parser.getCurrent().getType().equals(TokenTypes.END_OF_FILE())) {
                body.add(parser.parse(MeazyMain.getDefaultIdentifier("global_statement"), Statement.class));
                parser.moveOverOptionalNewLines();
            }

            return new Program(file, MeazyMain.VERSION, requiredAddons, body);
        });

        EVALUATE_PROGRAM_FUNCTION.register(MeazyMain.getDefaultIdentifier("evaluate_program"), (program, globalEnvironment) -> {
            for (String addonId : program.getRequiredAddons().keySet()) {
                Addon addon = MeazyMain.ADDON_MANAGER.getAddon(addonId);
                if (addon == null) throw new RuntimeException("Can't find required addon with id " + addonId);

                Version addonVersion = program.getRequiredAddons().get(addonId);
                if (addonVersion != null && !addon.getAddonInfo().getVersion().equals(addonVersion)) {
                    throw new RuntimeException("Can't find required addon with id " + addonId + " of version " + addonVersion +
                            " (found version " + addon.getAddonInfo().getVersion() + ")");
                }
            }

            FileEnvironment fileEnvironment = FILE_ENVIRONMENT_FACTORY.getEntry().getValue().create(globalEnvironment, program.getFile());
            globalEnvironment.getContext().getInterpreter().evaluate(program, fileEnvironment);

            return fileEnvironment;
        });

        RUN_PROGRAM_FUNCTION.register(MeazyMain.getDefaultIdentifier("run_program"), program -> {
            RuntimeContext context = new RuntimeContext();
            GlobalEnvironment globalEnvironment = context.getGlobalEnvironment();
            return Registries.EVALUATE_PROGRAM_FUNCTION.getEntry().getValue().evaluate(program, globalEnvironment);
        });
    }
}