package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.parser.ast.statement.BreakStatement;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class BreakStatementParsingFunction extends AbstractParsingFunction<BreakStatement> {
    public BreakStatementParsingFunction() {
        super("break_statement");
    }

    @Override
    public BreakStatement parse(ParsingContext context, @Nullable Object... extra) {
        context.getParser().next(TokenTypes.BREAK(), TextKt.translatable("meazy:parser.expected.keyword", "break"));
        return new BreakStatement();
    }
}
