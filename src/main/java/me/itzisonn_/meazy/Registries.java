package me.itzisonn_.meazy;

import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.command.AbstractCommand;
import me.itzisonn_.meazy.command.Commands;
import me.itzisonn_.meazy.lang.TextException;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.lang.Language;
import me.itzisonn_.meazy.lexer.*;
import me.itzisonn_.meazy.parser.*;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import me.itzisonn_.meazy.parser.ast.program.ProgramFactory;
import me.itzisonn_.meazy.registry.CommandRegistry;
import me.itzisonn_.meazy.registry.LanguageRegistry;
import me.itzisonn_.meazy.runtime.ClassLoaderHelper;
import me.itzisonn_.meazy.runtime.RunProgramFunction;
import me.itzisonn_.meazy.runtime.environment.factory.*;
import me.itzisonn_.meazy.version.Version;
import me.itzisonn_.registry.RegistryEntry;
import me.itzisonn_.meazy.parser.ast.program.Program;
import me.itzisonn_.registry.multiple_entry.OrderedRegistry;
import me.itzisonn_.registry.multiple_entry.SetRegistry;
import me.itzisonn_.registry.single_entry.SingleEntryRegistry;
import me.itzisonn_.registry.single_entry.SingleEntryRegistryImpl;
import me.itzisonn_.meazy.runtime.environment.*;

import java.lang.constant.ClassDesc;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
     * @see AbstractCommand
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
     * Registry for {@link ProgramFactory}
     */
    public static final SingleEntryRegistry<ProgramFactory> PROGRAM_FACTORY = new SingleEntryRegistryImpl<>();



    /**
     * Registry for function that compiles {@link Program} to bytecode
     * @see CompileProgramFunction
     */
    public static final SingleEntryRegistry<CompileProgramFunction> COMPILE_PROGRAM_FUNCTION = new SingleEntryRegistryImpl<>();

    /**
     * Registry for function that runs {@link Program}
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
     * Registry for {@link LocalVariableDeclarationEnvironmentFactory}
     */
    public static final SingleEntryRegistry<LocalVariableDeclarationEnvironmentFactory> LOCAL_VARIABLE_DECLARATION_ENVIRONMENT_FACTORY = new SingleEntryRegistryImpl<>();



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
                            token = new Token(lineNumber, i, end, tokenType, matched);
                        }
                    }
                }

                if (token == null) {
                    String errorString = string.split("\n")[0];
                    if (errorString.length() > 20) errorString = errorString.substring(0, 20) + "...";

                    throw new UnknownTokenException(lineNumber, errorString);
                }

                i += token.getValue().length() - 1;
                if (!token.getType().isShouldSkip()) tokens.add(token);

                lineNumber += token.getValue().length() - token.getValue().replace("\n", "").length();
            }

            tokens.add(new Token(lineNumber, lines.length(), lines.length(), TokenTypes.END_OF_FILE(), ""));
            return tokens;
        });

        COMPILE_PROGRAM_FUNCTION.register(MeazyMain.getDefaultIdentifier("compile_program"), program -> {
            for (String addonId : program.getRequiredAddons().keySet()) {
                Addon addon = MeazyMain.ADDON_MANAGER.getAddon(addonId);
                if (addon == null) throw new TextException(Text.translatable("meazy_addon:addons.cant_find", addonId)){};

                Version addonVersion = program.getRequiredAddons().get(addonId);
                if (addonVersion != null && !addon.getAddonInfo().getVersion().equals(addonVersion)) {
                    throw new TextException(Text.translatable("meazy_addon:addons.cant_find_version", addonId, addonVersion, addon.getAddonInfo().getVersion())){};
                }
            }

            GlobalEnvironment globalEnvironment = GLOBAL_ENVIRONMENT_FACTORY.getEntry().getValue().create();

            BytecodeBuilders bytecodeBuilders = BytecodeBuilders.of(null, null);
            InstructionsSet instructionsSet = new InstructionsSet(bytecodeBuilders);
            program.emit(instructionsSet, globalEnvironment, null);

            for (Instruction instruction : instructionsSet.getInstructions()) {
                instruction.emit(bytecodeBuilders);
            }

            return bytecodeBuilders.getClasses();
        });

        RUN_PROGRAM_FUNCTION.register(MeazyMain.getDefaultIdentifier("run_program"), classes -> {
            for (ClassDesc classDesc : classes.keySet()) {
                byte[] classFile = classes.get(classDesc);

                Class<?> loadedClass = ClassLoaderHelper.defineClass(classFile);
                try {
                    Method method = loadedClass.getDeclaredMethod("main");
                    if (method.getReturnType() != void.class) {
                        System.err.println("Main method has invalid signature in class" + classDesc); //TODO
                        continue;
                    }
                    if (!method.canAccess(null)) {
                        System.err.println("Main method is inaccessible in class " + classDesc);
                        continue;
                    }

                    method.invoke(null);
                }
                catch (NoSuchMethodException _) {
                    System.err.println("No method main in class " + classDesc);
                }
                catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}