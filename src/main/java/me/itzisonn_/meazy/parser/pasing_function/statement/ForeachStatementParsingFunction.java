package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.InvalidSyntaxException;
import me.itzisonn_.meazy.parser.ast.statement.ForeachStatement;
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class ForeachStatementParsingFunction extends AbstractParsingFunction<Statement> {
    public ForeachStatementParsingFunction() {
        super("foreach_statement");
    }

    @Override
    public Statement parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();

        parser.next(TokenTypes.FOR(), TextKt.translatable("meazy:parser.expected.keyword", "for"));
        parser.next(TokenTypes.LEFT_PARENTHESIS(), TextKt.translatable("meazy:parser.expected.start", "left_parenthesis", "for_condition"));

        boolean isConstant = parser.getCurrentAndNext(TokenTypes.VARIABLE(), TextKt.translatable("meazy:parser.expected.keyword", "variable")).getValue().equals("val");
        String id = parser.getCurrentAndNext(TokenTypes.ID(), TextKt.translatable("meazy:parser.expected", "id")).getValue();

        int lineNumber = parser.getCurrent().getLine();
        DataType dataType = ParsingHelper.parseDataType(context);
        if (dataType == null) throw new InvalidSyntaxException(lineNumber, TextKt.translatable("meazy:parser.exception.foreach_variable_without_datatype"));

        parser.next(TokenTypes.IN(), TextKt.translatable("meazy:parser.expected.after_statement", "in", "variable_declaration"));
        Expression collection = parser.parse(MeazyMain.getDefaultIdentifier("expression"), Expression.class);

        parser.next(TokenTypes.RIGHT_PARENTHESIS(), TextKt.translatable("meazy:parser.expected.end", "right_parenthesis", "for_condition"));

        parser.next(TokenTypes.LEFT_BRACE(), TextKt.translatable("meazy:parser.expected.start", "left_brace", "for_body"));
        List<LocalStatement> body = ParsingHelper.parseBody(context);
        parser.next(TokenTypes.RIGHT_BRACE(), TextKt.translatable("meazy:parser.expected.end", "right_brace", "for_body"));

        return new ForeachStatement(isConstant, id, dataType, collection, body);
    }
}
