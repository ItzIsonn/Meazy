package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.statement.WhileStatement;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class WhileStatementParsingFunction extends AbstractParsingFunction<WhileStatement> {
    public WhileStatementParsingFunction() {
        super("while_statement");
    }

    @Override
    public WhileStatement parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();

        parser.getCurrentAndNext(TokenTypes.WHILE(), Text.translatable("meazy:parser.expected.keyword", "while"));

        parser.getCurrentAndNext(TokenTypes.LEFT_PARENTHESIS(), Text.translatable("meazy:parser.expected.start", "left_parenthesis", "while_condition"));
        Expression condition = parser.parse(MeazyMain.getDefaultIdentifier("expression"), Expression.class);
        parser.getCurrentAndNext(TokenTypes.RIGHT_PARENTHESIS(), Text.translatable("meazy:parser.expected.end", "right_parenthesis", "while_condition"));

        parser.getCurrentAndNext(TokenTypes.LEFT_BRACE(), Text.translatable("meazy:parser.expected.start", "left_brace", "while_body"));
        List<Statement> body = ParsingHelper.parseBody(context);
        parser.getCurrentAndNext(TokenTypes.RIGHT_BRACE(), Text.translatable("meazy:parser.expected.end", "right_brace", "while_body"));

        return new WhileStatement(condition, body);
    }
}
