package me.itzisonn_.meazy.parser.pasing_function;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.InvalidStatementException;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.util.*;

@NullMarked
public final class ParsingHelper {
    private ParsingHelper() {}

    public static Set<Modifier> parseModifiers(ParsingContext context) {
        Parser parser = context.getParser();
        Set<Modifier> modifiers = new HashSet<>();

        while (parser.getCurrent().getType().equals(TokenTypes.ID())) {
            String id = parser.getCurrent().getValue();
            Modifier modifier = Modifiers.parse(id);

            if (modifier == null) {
                if (modifiers.isEmpty()) return modifiers;
                throw new InvalidStatementException(parser.getCurrent().getLine(), TextKt.translatable("meazy:parser.modifier.doesnt_exist", id));
            }

            parser.next();
            modifiers.add(modifier);
        }

        return modifiers;
    }

    public static Set<Modifier> getModifiersFromExtra(@Nullable Object[] extra) {
        if (extra.length == 0) throw new IllegalArgumentException("Expected Set of Modifiers as extra argument");
        if (!(extra[0] instanceof Set<?> set)) throw new IllegalArgumentException("Expected Set of Modifiers as extra argument");

        Set<Modifier> result = new HashSet<>();
        for (Object o : set) {
            if (o instanceof Modifier modifier) result.add(modifier);
            else throw new IllegalArgumentException("Expected Set of Modifiers as extra argument");
        }

        return result;
    }

    public static List<ParameterExpression> parseParameters(ParsingContext context) {
        Parser parser = context.getParser();

        parser.getCurrentAndNext(TokenTypes.LEFT_PARENTHESIS(), TextKt.translatable("meazy:parser.expected.start_expression", "left_parenthesis", "parameters"));
        List<ParameterExpression> parameters = new ArrayList<>();

        if (!parser.getCurrent().getType().equals(TokenTypes.RIGHT_PARENTHESIS())) {
            parameters.add(parser.parse(MeazyMain.getDefaultIdentifier("parameter_expression"), ParameterExpression.class));

            while (parser.getCurrent().getType().equals(TokenTypes.COMMA())) {
                parser.next();
                parameters.add(parser.parse(MeazyMain.getDefaultIdentifier("parameter_expression"), ParameterExpression.class));
            }
        }

        parser.getCurrentAndNext(TokenTypes.RIGHT_PARENTHESIS(), TextKt.translatable("meazy:parser.expected.end_expression", "right_parenthesis", "parameters"));
        return parameters;
    }

    public static List<Expression> parseArgs(ParsingContext context) {
        Parser parser = context.getParser();
        parser.getCurrentAndNext(TokenTypes.LEFT_PARENTHESIS(), TextKt.translatable("meazy:parser.expected.start_expression", "left_parenthesis", "args"));
        List<Expression> args = new ArrayList<>();

        if (!parser.getCurrent().getType().equals(TokenTypes.RIGHT_PARENTHESIS())) {
            args.add(parser.parse(MeazyMain.getDefaultIdentifier("expression"), Expression.class));

            while (parser.getCurrent().getType().equals(TokenTypes.COMMA())) {
                parser.next();
                args.add(parser.parse(MeazyMain.getDefaultIdentifier("expression"), Expression.class));
            }
        }

        parser.getCurrentAndNext(TokenTypes.RIGHT_PARENTHESIS(), TextKt.translatable("meazy:parser.expected.end_expression", "right_parenthesis", "args"));
        return args;
    }

    @Nullable
    public static DataType parseDataType(ParsingContext context) {
        Parser parser = context.getParser();

        if (parser.getCurrent().getType().equals(TokenTypes.COLON())) {
            parser.getCurrentAndNext();
            String dataTypeId = parser.getCurrentAndNext(TokenTypes.ID(), TextKt.translatable("meazy:parser.expected.after", "id", "colon")).getValue();

            if (parser.getCurrent().getType().equals(TokenTypes.QUESTION())) {
                parser.getCurrentAndNext();
                return DataType.ofNullable(ClassDesc.of(dataTypeId));
            }

            return DataType.ofNonNull(ClassDesc.of(dataTypeId));
        }

        return null;
    }

    public static List<LocalStatement> parseBody(ParsingContext context) {
        Parser parser = context.getParser();

        List<LocalStatement> body = new ArrayList<>();
        parser.getCurrentAndNext(TokenTypes.NEW_LINE(), TextKt.translatable("meazy:parser.expected", "new_line"));
        parser.moveOverOptionalNewLines();

        while (!parser.getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !parser.getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) {
            body.add(parser.parse(MeazyMain.getDefaultIdentifier("local_statement"), LocalStatement.class));
            parser.getCurrentAndNext(TokenTypes.NEW_LINE(), TextKt.translatable("meazy:parser.expected", "new_line"));
            parser.moveOverOptionalNewLines();
        }

        return body;
    }

    public static String parseString(ParsingContext context) {
        Parser parser = context.getParser();
        Token token = parser.getCurrent();

        String value = parser.getCurrentAndNext(TokenTypes.STRING(), TextKt.translatable("meazy:parser.expected", "string")).getValue();
        if (!value.endsWith("\"")) throw new InvalidStatementException(token.getLine(), TextKt.translatable("meazy:parser.exception.string_quote_not_closed", value.substring(1)));
        return value.substring(1, value.length() - 1);
    }
}
