package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.version.Version;
import me.itzisonn_.meazy.parser.ast.statement.RequireStatement;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class RequireStatementParsingFunction extends AbstractParsingFunction<RequireStatement> {
    public RequireStatementParsingFunction() {
        super("require_statement");
    }

    @Override
    public RequireStatement parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();

        parser.next(TokenTypes.REQUIRE(), Text.translatable("meazy:parser.expected.keyword", "require"));
        String id = parser.getCurrentAndNext(TokenTypes.ID(), Text.translatable("meazy:parser.expected.after_keyword", "id", "require")).getValue();

        Version version;
        if (parser.getCurrent().getType().equals(TokenTypes.STRING())) version = Version.of(ParsingHelper.parseString(context));
        else version = null;

        return new RequireStatement(id, version);
    }
}
