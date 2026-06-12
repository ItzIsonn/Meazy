package me.itzisonn_.meazy.parser.pasing_function;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.parser.*;
import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.Parameter;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
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
            Modifier modifier = Modifiers.INSTANCE.parse(id);

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

    private static Parameter parseParameter(ParsingContext context) {
        Parser parser = context.getParser();

        if (!parser.getCurrent().getType().equals(TokenTypes.VARIABLE())) {
            throw new UnexpectedTokenException(parser.getCurrent().getLine(), TextKt.translatable("meazy:parser.expected.start_expression", "variable", "parameter"));
        }

        boolean isConstant = parser.consume().getValue().equals("val");
        String id = parser.consume(TokenTypes.ID(), TextKt.translatable("meazy:parser.expected.after_keyword", "id", "variable")).getValue();

        int lineNumber = parser.getCurrent().getLine();
        DataType dataType = ParsingHelper.parseDataType(context);
        if (dataType == null) throw new InvalidSyntaxException(lineNumber, TextKt.translatable("meazy:parser.exception.parameter_without_datatype"));

        return new Parameter(id, dataType, isConstant);
    }

    public static List<Parameter> parseParameters(ParsingContext context) {
        Parser parser = context.getParser();

        parser.consume(TokenTypes.LEFT_PARENTHESIS(), TextKt.translatable("meazy:parser.expected.start_expression", "left_parenthesis", "parameters"));
        List<Parameter> parameters = new ArrayList<>();

        if (!parser.getCurrent().getType().equals(TokenTypes.RIGHT_PARENTHESIS())) {
            parameters.add(parseParameter(context));

            while (parser.getCurrent().getType().equals(TokenTypes.COMMA())) {
                parser.next();
                parameters.add(parseParameter(context));
            }
        }

        parser.consume(TokenTypes.RIGHT_PARENTHESIS(), TextKt.translatable("meazy:parser.expected.end_expression", "right_parenthesis", "parameters"));
        return parameters;
    }

    public static List<Expression> parseArgs(ParsingContext context) {
        Parser parser = context.getParser();
        parser.consume(TokenTypes.LEFT_PARENTHESIS(), TextKt.translatable("meazy:parser.expected.start_expression", "left_parenthesis", "args"));
        List<Expression> args = new ArrayList<>();

        if (!parser.getCurrent().getType().equals(TokenTypes.RIGHT_PARENTHESIS())) {
            args.add(parser.parse(MeazyMain.getDefaultIdentifier("expression"), Expression.class));

            while (parser.getCurrent().getType().equals(TokenTypes.COMMA())) {
                parser.next();
                args.add(parser.parse(MeazyMain.getDefaultIdentifier("expression"), Expression.class));
            }
        }

        parser.consume(TokenTypes.RIGHT_PARENTHESIS(), TextKt.translatable("meazy:parser.expected.end_expression", "right_parenthesis", "args"));
        return args;
    }

    @Nullable
    public static DataType parseDataType(ParsingContext context) {
        Parser parser = context.getParser();

        if (parser.getCurrent().getType().equals(TokenTypes.COLON())) {
            parser.consume();
            String dataTypeId = parser.consume(TokenTypes.ID(), TextKt.translatable("meazy:parser.expected.after", "id", "colon")).getValue();

            if (parser.getCurrent().getType().equals(TokenTypes.QUESTION())) {
                parser.consume();
                return DataType.Companion.ofNullable(ClassDesc.of(dataTypeId));
            }

            return DataType.Companion.ofNonNull(ClassDesc.of(dataTypeId));
        }

        return null;
    }

    public static List<LocalStatement> parseBody(ParsingContext context) {
        Parser parser = context.getParser();

        List<LocalStatement> body = new ArrayList<>();
        parser.consume(TokenTypes.NEW_LINE(), TextKt.translatable("meazy:parser.expected", "new_line"));
        parser.skipNewLines();

        while (!parser.getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !parser.getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) {
            body.add(parser.parse(MeazyMain.getDefaultIdentifier("local_statement"), LocalStatement.class));
            parser.consume(TokenTypes.NEW_LINE(), TextKt.translatable("meazy:parser.expected", "new_line"));
            parser.skipNewLines();
        }

        return body;
    }

    public static String parseString(ParsingContext context) {
        Parser parser = context.getParser();
        Token token = parser.getCurrent();

        String value = parser.consume(TokenTypes.INSTANCE.getString(), TextKt.translatable("meazy:parser.expected", "string")).getValue();
        if (!value.endsWith("\"")) throw new InvalidStatementException(token.getLine(), TextKt.translatable("meazy:parser.exception.string_quote_not_closed", value.substring(1)));
        return value.substring(1, value.length() - 1);
    }
}
