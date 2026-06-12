package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.parser.ast.statement.BaseCallStatement;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class BaseCallStatementParsingFunction extends AbstractParsingFunction<BaseCallStatement> {
    public BaseCallStatementParsingFunction() {
        super("base_call_statement");
    }

    @Override
    public BaseCallStatement parse(ParsingContext context, @Nullable Object... extra) {
        context.getParser().next(TokenTypes.BASE(), TextKt.translatable("meazy:parser.expected.start_statement", "base", "base_call"));
        return new BaseCallStatement(ParsingHelper.parseArgs(context));
    }
}
