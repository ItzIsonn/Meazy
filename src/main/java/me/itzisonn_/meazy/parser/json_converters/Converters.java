package me.itzisonn_.meazy.parser.json_converters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.expression.*;
import me.itzisonn_.meazy.parser.ast.expression.call_expression.ClassCallExpression;
import me.itzisonn_.meazy.parser.ast.expression.call_expression.FunctionCallExpression;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.literal.BooleanLiteral;
import me.itzisonn_.meazy.parser.ast.expression.literal.NullLiteral;
import me.itzisonn_.meazy.parser.ast.expression.literal.NumberLiteral;
import me.itzisonn_.meazy.parser.ast.expression.literal.StringLiteral;
import me.itzisonn_.meazy.parser.ast.statement.*;
import me.itzisonn_.meazy.parser.json_converters.expression.*;
import me.itzisonn_.meazy.parser.json_converters.expression.call_expression.ClassCallExpressionConverter;
import me.itzisonn_.meazy.parser.json_converters.expression.call_expression.FunctionCallExpressionConverter;
import me.itzisonn_.meazy.parser.json_converters.expression.identifier.ClassIdentifierConverter;
import me.itzisonn_.meazy.parser.json_converters.expression.identifier.FunctionIdentifierConverter;
import me.itzisonn_.meazy.parser.json_converters.expression.identifier.VariableIdentifierConverter;
import me.itzisonn_.meazy.parser.json_converters.expression.literal.BooleanLiteralConverter;
import me.itzisonn_.meazy.parser.json_converters.expression.literal.NullLiteralConverter;
import me.itzisonn_.meazy.parser.json_converters.expression.literal.NumberLiteralConverter;
import me.itzisonn_.meazy.parser.json_converters.expression.literal.StringLiteralConverter;
import me.itzisonn_.meazy.parser.json_converters.statement.*;
import me.itzisonn_.meazy.registry.Pair;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryEntry;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

public class Converters {
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



    private static <T extends Statement> void register(Class<T> cls, Converter<T> converter) {
        Registries.CONVERTERS.register(RegistryIdentifier.ofDefault(converter.getId()), cls, converter);
    }

    public static void INIT() {
        if (isInit) throw new IllegalStateException("Converters already initialized!");
        isInit = true;

        register(BreakStatement.class, new BreakStatementConverter());
        register(ClassDeclarationStatement.class, new ClassDeclarationStatementConverter());
        register(ConstructorDeclarationStatement.class, new ConstructorDeclarationStatementConverter());
        register(ContinueStatement.class, new ContinueStatementConverter());
        register(ForeachStatement.class, new ForeachStatementConverter());
        register(ForStatement.class, new ForStatementConverter());
        register(FunctionDeclarationStatement.class, new FunctionDeclarationStatementConverter());
        register(IfStatement.class, new IfStatementConverter());
        register(Program.class, new ProgramConverter());
        register(ReturnStatement.class, new ReturnStatementConverter());
        register(VariableDeclarationStatement.class, new VariableDeclarationConverter());
        register(WhileStatement.class, new WhileStatementConverter());

        register(BooleanLiteral.class, new BooleanLiteralConverter());
        register(NullLiteral.class, new NullLiteralConverter());
        register(NumberLiteral.class, new NumberLiteralConverter());
        register(StringLiteral.class, new StringLiteralConverter());

        register(ArrayDeclarationExpression.class, new ArrayDeclarationExpressionConverter());
        register(ArrayPointerExpression.class, new ArrayPointerExpressionConverter());
        register(AssignmentExpression.class, new AssignmentExpressionConverter());
        register(BinaryExpression.class, new BinaryExpressionConverter());
        register(CallArgExpression.class, new CallArgExpressionConverter());
        register(ComparisonExpression.class, new ComparisonExpressionConverter());
        register(LogicalExpression.class, new LogicalExpressionConverter());
        register(MemberExpression.class, new MemberExpressionConverter());
        register(ClassCallExpression.class, new ClassCallExpressionConverter());
        register(FunctionCallExpression.class, new FunctionCallExpressionConverter());
        register(ClassIdentifier.class, new ClassIdentifierConverter());
        register(FunctionIdentifier.class, new FunctionIdentifierConverter());
        register(VariableIdentifier.class, new VariableIdentifierConverter());

        register(Statement.class, new StatementConverter());
        register(Expression.class, new ExpressionConverter());

        updateGson();
    }
}
