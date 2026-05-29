package me.itzisonn_.meazy.parser.pasing_function;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.pasing_function.expression.*;
import me.itzisonn_.meazy.parser.pasing_function.statement.*;
import me.itzisonn_.meazy.registry.Registries;
import org.jspecify.annotations.NonNull;

/**
 * Parsing functions registrar
 *
 * @see Registries#PARSING_FUNCTIONS
 */
public final class ParsingFunctions {
    private static boolean hasRegistered = false;

    private ParsingFunctions() {}



    /**
     * Initializes {@link Registries#PARSING_FUNCTIONS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#PARSING_FUNCTIONS} registry has already been initialized
     */
    public static void REGISTER() {
        if (hasRegistered) throw new IllegalStateException("ParsingFunctions have already been initialized");
        hasRegistered = true;

        register(new ProgramParsingFunction());

        register(new HeaderStatementParsingFunction());
        register(new ImportStatementParsingFunction());
        register(new RequireStatementParsingFunction());

        register(new GlobalStatementParsingFunction());
        register(new ClassDeclarationStatementParsingFunction());
        register(new ClassBodyStatementParsingFunction());
        register(new FunctionDeclarationStatementParsingFunction());
        register(new ParameterExpressionParsingFunction());
        register(new VariableDeclarationStatementParsingFunction());
        register(new ConstructorDeclarationStatementParsingFunction());
        register(new BaseCallStatementParsingFunction());
        register(new LocalStatementParsingFunction());
        register(new IfStatementParsingFunction());
        register(new ForeachStatementParsingFunction());
        register(new WhileStatementParsingFunction());
        register(new ReturnStatementParsingFunction());
        register(new ContinueStatementParsingFunction());
        register(new BreakStatementParsingFunction());
        register(new AssignmentStatementParsingFunction());
        register(new PostfixStatementParsingFunction());

        register(new ExpressionParsingFunction());
        register(new ListCreationExpressionParsingFunction());
        register(new MapCreationExpressionParsingFunction());
        register(new NullCheckExpressionParsingFunction());
        register(new LogicalExpressionParsingFunction());
        register(new ComparisonExpressionParsingFunction());
        register(new AdditionExpressionParsingFunction());
        register(new MultiplicationExpressionParsingFunction());
        register(new PowerExpressionParsingFunction());
        register(new InversionExpressionParsingFunction());
        register(new IsExpressionParsingFunction());
        register(new NegationExpressionParsingFunction());
        register(new MemberExpressionParsingFunction());
        register(new CallExpressionParsingFunction());
        register(new PrimaryExpressionParsingFunction());
    }

    private static void register(AbstractParsingFunction<? extends @NonNull Statement> parsingFunction) {
        Registries.PARSING_FUNCTIONS.register(MeazyMain.getDefaultIdentifier(parsingFunction.getId()), parsingFunction);
    }
}