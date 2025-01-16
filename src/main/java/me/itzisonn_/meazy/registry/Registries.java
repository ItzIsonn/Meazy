package me.itzisonn_.meazy.registry;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.command.Command;
import me.itzisonn_.meazy.command.Commands;
import me.itzisonn_.meazy.lexer.*;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ParsingFunction;
import me.itzisonn_.meazy.parser.ParsingFunctions;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.AccessModifiers;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.DataTypes;
import me.itzisonn_.meazy.parser.ast.statement.Program;
import me.itzisonn_.meazy.parser.ast.statement.ReturnStatement;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.Converters;
import me.itzisonn_.meazy.registry.multiple_entry.PairRegistry;
import me.itzisonn_.meazy.registry.multiple_entry.SetRegistry;
import me.itzisonn_.meazy.registry.single_entry.SingleEntryRegistry;
import me.itzisonn_.meazy.registry.single_entry.SingleEntryRegistryImpl;
import me.itzisonn_.meazy.runtime.environment.basic.*;
import me.itzisonn_.meazy.runtime.environment.interfaces.*;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.VariableDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.*;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.function.RuntimeFunctionValue;
import me.itzisonn_.meazy.runtime.values.statement_info.ReturnInfoValue;
import org.apache.logging.log4j.Level;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * All basic Registries
 */
public final class Registries {
    private static boolean isInit = false;



    /**
     * Registry for all Commands
     *
     * @see Command
     */
    public static final SetRegistry<Command> COMMANDS = new SetRegistry<>();



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
     * Registry for all custom DataTypes
     */
    public static final SetRegistry<DataType> DATA_TYPES = new SetRegistry<>();

    /**
     * Registry for all AccessModifiers
     */
    public static final SetRegistry<AccessModifier> ACCESS_MODIFIERS = new SetRegistry<>();

    /**
     * Registry for all ParsingFunctions
     *
     * @see ParsingFunction
     * @see Parser
     */
    public static final SetRegistry<ParsingFunction<? extends Statement>> PARSING_FUNCTIONS = new SetRegistry<>();

    /**
     * Registry for function that uses {@link Registries#PARSING_FUNCTIONS} to parse tokens into {@link Program}
     *
     * @see ParsingFunction
     * @see Parser
     */
    public static final SingleEntryRegistry<Function<List<Token>, Program>> PARSE_TOKENS_FUNCTION = new SingleEntryRegistryImpl<>();



    /**
     * Registry for all Converters that is used to compile and decompile Statements
     *
     * @see Converter
     * @see Statement
     */
    public static final PairRegistry<Class<? extends Statement>, Converter<? extends Statement>> CONVERTERS = new PairRegistry<>();



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
    public static final SingleEntryRegistry<Consumer<Program>> EVALUATE_PROGRAM_FUNCTION = new SingleEntryRegistryImpl<>();



    /**
     * Registry for {@link GlobalEnvironment}
     */
    public static final SingleEntryRegistry<GlobalEnvironment> GLOBAL_ENVIRONMENT = new SingleEntryRegistryImpl<>();

    /**
     * Registry for {@link ClassEnvironment} class
     */
    public static final SingleEntryRegistry<Class<? extends ClassEnvironment>> CLASS_ENVIRONMENT = new SingleEntryRegistryImpl<>();

    /**
     * Registry for {@link FunctionEnvironment} class
     */
    public static final SingleEntryRegistry<Class<? extends FunctionEnvironment>> FUNCTION_ENVIRONMENT = new SingleEntryRegistryImpl<>();

    /**
     * Registry for {@link VariableDeclarationEnvironment} class
     */
    public static final SingleEntryRegistry<Class<? extends VariableDeclarationEnvironment>> VARIABLE_DECLARATION_ENVIRONMENT = new SingleEntryRegistryImpl<>();

    /**
     * Registry for {@link LoopEnvironment} class
     */
    public static final SingleEntryRegistry<Class<? extends LoopEnvironment>> LOOP_ENVIRONMENT = new SingleEntryRegistryImpl<>();

    /**
     * Registry for {@link Environment} class
     */
    public static final SingleEntryRegistry<Class<? extends Environment>> ENVIRONMENT = new SingleEntryRegistryImpl<>();



    /**
     * Initializes all registries
     * <p>
     * <i>Don't use this method because it's called once at Meazy initialization</i>
     *
     * @throws IllegalStateException If registries have already been initialized
     */
    public static void INIT() throws IllegalStateException {
        if (isInit) throw new IllegalStateException("Registries have already been initialized!");
        isInit = true;

        Commands.INIT();
        TokenTypes.INIT();
        TokenTypeSets.INIT();
        DataTypes.INIT();
        AccessModifiers.INIT();
        ParsingFunctions.INIT();
        EvaluationFunctions.INIT();
        Converters.INIT();

        TOKENIZATION_FUNCTION.register(RegistryIdentifier.ofDefault("tokens_function"), lines -> {
            List<Token> tokens = new ArrayList<>();
            TokenType tokenType;
            int lineNumber = 1;

            for (int i = 0; i < lines.length(); i++) {
                if (i == lines.length() - 1) {
                    tokenType = TokenTypes.parse(String.valueOf(lines.charAt(i)));
                    if (tokenType != null && !tokenType.isShouldSkip()) tokens.add(new Token(lineNumber, tokenType, String.valueOf(lines.charAt(i))));
                    break;
                }

                TokenType lastMatched = null;
                String lastString = "";
                int lastFound = -1;
                for (int j = i; j < lines.length(); j++) {
                    lastString += lines.charAt(j);
                    tokenType = TokenTypes.parse(lastString);

                    if (lastMatched != null && tokenType == null) {
                        break;
                    }

                    if (tokenType != null) {
                        lastFound = j + 1;
                        lastMatched = tokenType;
                    }
                }

                if (lastFound == -1) {
                    throw new UnknownTokenException("At line " + lineNumber + ": " + lastString.replaceAll("\n", "\\\\n"));
                }

                if (!lastMatched.isShouldSkip()) tokens.add(new Token(lineNumber, lastMatched, lines.substring(i, lastFound)));
                if (lastMatched == TokenTypes.NEW_LINE()) lineNumber += lines.substring(i, lastFound).length();
                else if (lastMatched == TokenTypes.MULTI_LINE_COMMENT()) lineNumber += Utils.countMatches(lines.substring(i, lastFound), "\n");
                i = lastFound - 1;
            }
            tokens.add(new Token(lineNumber, TokenTypes.END_OF_FILE(), ""));

            return tokens;
        });

        PARSE_TOKENS_FUNCTION.register(RegistryIdentifier.ofDefault("parse_tokens"), tokens -> {
            if (tokens == null) throw new NullPointerException("Tokens can't be null!");
            Parser.setTokens(tokens);

            Parser.moveOverOptionalNewLine();

            List<Statement> body = new ArrayList<>();
            while (!Parser.getCurrent().getType().equals(TokenTypes.END_OF_FILE())) {
                Statement statement = Parser.parse(RegistryIdentifier.ofDefault("global_statement"), Statement.class);
                body.add(statement);
            }

            return new Program(MeazyMain.getVersion(), body);
        });

        EVALUATE_PROGRAM_FUNCTION.register(RegistryIdentifier.ofDefault("evaluate_program"), program -> {
            Interpreter.evaluate(program, Registries.GLOBAL_ENVIRONMENT.getEntry().getValue());

            RuntimeValue<?> runtimeValue = Registries.GLOBAL_ENVIRONMENT.getEntry().getValue().getFunction("main", new ArrayList<>());
            if (runtimeValue == null) {
                MeazyMain.getLogger().log(Level.WARN, "File doesn't contain main function");
                return;
            }

            if (runtimeValue instanceof RuntimeFunctionValue runtimeFunctionValue) {
                FunctionEnvironment functionEnvironment;
                try {
                    functionEnvironment = Registries.FUNCTION_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class).newInstance(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue());
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }

                if (!runtimeFunctionValue.getArgs().isEmpty()) throw new InvalidArgumentException("Main function must have no args");

                for (int i = 0; i < runtimeFunctionValue.getBody().size(); i++) {
                    Statement statement = runtimeFunctionValue.getBody().get(i);
                    if (statement instanceof ReturnStatement returnStatement) {
                        if (returnStatement.getValue() != null) {
                            throw new InvalidSyntaxException("Found return statement but function must return nothing");
                        }
                        if (i + 1 < runtimeFunctionValue.getBody().size()) throw new InvalidSyntaxException("Return statement must be last in body");
                        break;
                    }
                    RuntimeValue<?> value = Interpreter.evaluate(statement, functionEnvironment);
                    if (value instanceof ReturnInfoValue returnInfoValue) {
                        if (returnInfoValue.getFinalValue() != null) {
                            throw new InvalidSyntaxException("Found return statement but function must return nothing");
                        }
                        break;
                    }
                }
            }
            else MeazyMain.getLogger().log(Level.WARN, "File contains invalid main function");
        });

        Registries.GLOBAL_ENVIRONMENT.register(RegistryIdentifier.ofDefault("global_environment"), new BasicGlobalEnvironment());
        Registries.CLASS_ENVIRONMENT.register(RegistryIdentifier.ofDefault("class_environment"), BasicClassEnvironment.class);
        Registries.FUNCTION_ENVIRONMENT.register(RegistryIdentifier.ofDefault("function_environment"), BasicFunctionEnvironment.class);
        Registries.VARIABLE_DECLARATION_ENVIRONMENT.register(RegistryIdentifier.ofDefault("variable_declaration_environment"), BasicVariableDeclarationEnvironment.class);
        Registries.LOOP_ENVIRONMENT.register(RegistryIdentifier.ofDefault("loop_environment"), BasicLoopEnvironment.class);
        Registries.ENVIRONMENT.register(RegistryIdentifier.ofDefault("environment"), BasicEnvironment.class);
    }
}