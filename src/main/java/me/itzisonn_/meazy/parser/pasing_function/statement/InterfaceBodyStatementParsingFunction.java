package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.InvalidStatementException;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;

@NullMarked
public class InterfaceBodyStatementParsingFunction extends AbstractParsingFunction<Statement> {
    public InterfaceBodyStatementParsingFunction() {
        super("interface_body_statement");
    }

    @Override
    public Statement parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();
        Set<Modifier> modifiers = ParsingHelper.parseModifiers(context);

        if (parser.getCurrent().getType().equals(TokenTypes.FUNCTION())) {
            return parser.parse(MeazyMain.getDefaultIdentifier("function_declaration_statement"), FunctionDeclarationStatement.class, modifiers, true);
        }

        throw new InvalidStatementException(parser.getCurrent().getLine(), Text.translatable("meazy:parser.expected.statement", "interface_body"));
    }
}
