package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.statement.ReturnStatement;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ReturnStatementParsingFunction extends AbstractParsingFunction<ReturnStatement> {
    public ReturnStatementParsingFunction() {
        super("return_statement");
    }

    @Override
    public ReturnStatement parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();
        parser.next(TokenTypes.RETURN(), TextKt.translatable("meazy:parser.expected.keyword", "return"));

        Expression expression = null;
        if (!parser.getCurrent().getType().equals(TokenTypes.NEW_LINE())) {
            expression = parser.parse(MeazyMain.getDefaultIdentifier("expression"), Expression.class);
        }

        return new ReturnStatement(expression);
    }
}
