package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.parser.ast.statement.ContinueStatement;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ContinueStatementParsingFunction extends AbstractParsingFunction<ContinueStatement> {
    public ContinueStatementParsingFunction() {
        super("continue_statement");
    }

    @Override
    public ContinueStatement parse(ParsingContext context, @Nullable Object... extra) {
        context.getParser().next(TokenTypes.CONTINUE(), TextKt.translatable("meazy:parser.expected.keyword", "continue"));
        return new ContinueStatement();
    }
}
