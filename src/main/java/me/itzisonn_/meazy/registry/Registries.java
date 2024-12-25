package me.itzisonn_.meazy.registry;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.Utils;
import me.itzisonn_.meazy.lexer.*;
import me.itzisonn_.meazy.parser.BasicParser;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.AccessModifiers;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.DataTypes;
import me.itzisonn_.meazy.parser.ast.statement.Program;
import me.itzisonn_.meazy.parser.ast.statement.ReturnStatement;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.Converters;
import me.itzisonn_.meazy.runtime.environment.basic.*;
import me.itzisonn_.meazy.runtime.environment.interfaces.*;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.VariableDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.*;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.function.FunctionValue;
import me.itzisonn_.meazy.runtime.values.statement_info.ReturnInfoValue;
import org.apache.logging.log4j.Level;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Registries {
    public static final SingleRegistry<Function<String, ArrayList<Token>>> TOKENS_FUNCTION = new SingleRegistry<>();

    public static final SingleRegistry<Parser> PARSER = new SingleRegistry<>();
    public static final PairRegistry<Class<? extends Statement>, Converter<? extends Statement>> CONVERTERS = new PairRegistry<>();

    public static final PairRegistry<Class<? extends Statement>, EvaluationFunction<? extends Statement>> EVALUATION_FUNCTION = new PairRegistry<>();
    public static final SingleRegistry<Consumer<Program>> RUN_FUNCTION = new SingleRegistry<>();

    public static final SingleRegistry<GlobalEnvironment> GLOBAL_ENVIRONMENT = new SingleRegistry<>();
    public static final SingleRegistry<Class<? extends ClassEnvironment>> CLASS_ENVIRONMENT = new SingleRegistry<>();
    public static final SingleRegistry<Class<? extends FunctionEnvironment>> FUNCTION_ENVIRONMENT = new SingleRegistry<>();
    public static final SingleRegistry<Class<? extends VariableDeclarationEnvironment>> VARIABLE_DECLARATION_ENVIRONMENT = new SingleRegistry<>();
    public static final SingleRegistry<Class<? extends LoopEnvironment>> LOOP_ENVIRONMENT = new SingleRegistry<>();
    public static final SingleRegistry<Class<? extends Environment>> ENVIRONMENT = new SingleRegistry<>();

    public static final SetRegistry<TokenType> TOKEN_TYPE = new SetRegistry<>();
    public static final SetRegistry<DataType> DATA_TYPE = new SetRegistry<>();
    public static final SetRegistry<AccessModifier> ACCESS_MODIFIER = new SetRegistry<>();



    public static void INIT() {
        TokenTypes.INIT();
        DataTypes.INIT();
        AccessModifiers.INIT();
        EvaluationFunctions.INIT();
        Converters.INIT();

        Registries.TOKENS_FUNCTION.register(RegistryIdentifier.ofDefault("tokens_function"), lines -> {
            ArrayList<Token> tokens = new ArrayList<>();
            TokenType tokenType;
            TokenType lastMatched = null;
            int lineNumber = 1;

            for (int i = 0; i < lines.length(); i++) {
                if (i == lines.length() - 1) {
                    tokenType = TokenTypes.parse(String.valueOf(lines.charAt(i)));
                    if (tokenType != null && !tokenType.isShouldSkip()) tokens.add(new Token(lineNumber, tokenType, String.valueOf(lines.charAt(i))));
                    break;
                }

                String lastString = "";
                int lastFound = -1;
                for (int j = i; j < lines.length(); j++) {
                    lastString += lines.charAt(j);
                    tokenType = TokenTypes.parse(lastString);
                    if (tokenType != null) {
                        lastFound = j + 1;
                        lastMatched = tokenType;
                    }
                }

                if (lastFound == -1) {
                    throw new UnknownTokenException("At line " + lineNumber + ": " + lastString.replaceAll("\n", "\\\\n"));
                }

                if (!lastMatched.isShouldSkip()) tokens.add(new Token(lineNumber, lastMatched, lines.substring(i, lastFound)));
                if (lastMatched == TokenTypes.NEW_LINE()) lineNumber++;
                else if (lastMatched == TokenTypes.MULTI_LINE_COMMENT()) lineNumber += Utils.countMatches(lines.substring(i, lastFound), "\n");
                i = lastFound - 1;
                lastMatched = null;
            }
            tokens.add(new Token(lineNumber, TokenTypes.END_OF_FILE(), ""));

            ArrayList<Token> newTokens = new ArrayList<>(List.of(tokens.getFirst()));
            for (int i = 1; i < tokens.size(); i++) {
                Token token = tokens.get(i);
                if (token.getType().equals(TokenTypes.NEW_LINE()) && tokens.get(i - 1).getType().equals(TokenTypes.NEW_LINE())) continue;
                newTokens.add(token);
            }

            return newTokens;
        });

        Registries.PARSER.register(RegistryIdentifier.ofDefault("parser"), new BasicParser());

        Registries.RUN_FUNCTION.register(RegistryIdentifier.ofDefault("run_function"), program -> {
            Interpreter.evaluate(program, Registries.GLOBAL_ENVIRONMENT.getEntry().getValue());

            RuntimeValue<?> runtimeValue = Registries.GLOBAL_ENVIRONMENT.getEntry().getValue().getFunction("main", new ArrayList<>());
            if (runtimeValue == null) {
                MeazyMain.getInstance().getLogger().log(Level.WARN, "File doesn't contain main function");
                return;
            }

            if (runtimeValue instanceof FunctionValue functionValue) {
                FunctionEnvironment functionEnvironment;
                try {
                    functionEnvironment = Registries.FUNCTION_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class).newInstance(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue());
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }

                if (!functionValue.getArgs().isEmpty()) throw new InvalidArgumentException("Main function must have no args");

                for (int i = 0; i < functionValue.getBody().size(); i++) {
                    Statement statement = functionValue.getBody().get(i);
                    if (statement instanceof ReturnStatement returnStatement) {
                        if (returnStatement.getValue() != null) {
                            throw new InvalidSyntaxException("Found return statement but function must return nothing");
                        }
                        if (i + 1 < functionValue.getBody().size()) throw new InvalidSyntaxException("Return statement must be last in body");
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
            else MeazyMain.getInstance().getLogger().log(Level.WARN, "File contains invalid main function");
        });

        Registries.GLOBAL_ENVIRONMENT.register(RegistryIdentifier.ofDefault("global_environment"), new BasicGlobalEnvironment());
        Registries.CLASS_ENVIRONMENT.register(RegistryIdentifier.ofDefault("class_environment"), BasicClassEnvironment.class);
        Registries.FUNCTION_ENVIRONMENT.register(RegistryIdentifier.ofDefault("function_environment"), BasicFunctionEnvironment.class);
        Registries.VARIABLE_DECLARATION_ENVIRONMENT.register(RegistryIdentifier.ofDefault("variable_declaration_environment"), BasicVariableDeclarationEnvironment.class);
        Registries.LOOP_ENVIRONMENT.register(RegistryIdentifier.ofDefault("loop_environment"), BasicLoopEnvironment.class);
        Registries.ENVIRONMENT.register(RegistryIdentifier.ofDefault("environment"), BasicEnvironment.class);
    }
}