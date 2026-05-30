package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.UnexpectedTokenException;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.statement.ImportStatement;
import me.itzisonn_.meazy.parser.ast.statement.RequireStatement;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class HeaderStatementParsingFunction extends AbstractParsingFunction<Statement> {
    public HeaderStatementParsingFunction() {
        super("header_statement");
    }

    @Override
    public Statement parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();

        if (parser.getCurrent().getType().equals(TokenTypes.IMPORT())) {
            return parser.parse(MeazyMain.getDefaultIdentifier("import_statement"), ImportStatement.class);
        }
        if (parser.getCurrent().getType().equals(TokenTypes.REQUIRE())) {
            return parser.parse(MeazyMain.getDefaultIdentifier("require_statement"), RequireStatement.class);
        }

        throw new UnexpectedTokenException(parser.getCurrent().getLine(), Text.literal("Expected header statement")); //TODO;
    }
}
