package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.ast.statement.ConstructorDeclarationStatement;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NullMarked
public class ConstructorDeclarationStatementParsingFunction extends AbstractParsingFunction<ConstructorDeclarationStatement> {
    public ConstructorDeclarationStatementParsingFunction() {
        super("constructor_declaration_statement");
    }

    @Override
    public ConstructorDeclarationStatement parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();

        Set<Modifier> modifiers = ParsingHelper.getModifiersFromExtra(extra);
        parser.next(TokenTypes.CONSTRUCTOR(), Text.translatable("meazy:parser.expected.keyword", "constructor"));

        List<ParameterExpression> parameters = ParsingHelper.parseParameters(context);

        if (!parser.getCurrent().getType().equals(TokenTypes.LEFT_BRACE())) {
            return new ConstructorDeclarationStatement(modifiers, parameters, new ArrayList<>());
        }

        parser.next(TokenTypes.LEFT_BRACE(), Text.translatable("meazy:parser.expected.start", "left_brace", "constructor_body"));
        parser.next(TokenTypes.NEW_LINE(),  Text.translatable("meazy:parser.expected", "new_line"));
        parser.moveOverOptionalNewLines();

        List<Statement> body = new ArrayList<>();
        while (!parser.getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !parser.getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) {
            if (parser.getCurrent().getType().equals(TokenTypes.BASE())) body.add(parser.parse(MeazyMain.getDefaultIdentifier("base_call_statement")));
            else body.add(parser.parse(MeazyMain.getDefaultIdentifier("local_statement")));
            parser.next(TokenTypes.NEW_LINE(),  Text.translatable("meazy:parser.expected", "new_line"));
            parser.moveOverOptionalNewLines();
        }

        parser.next(TokenTypes.RIGHT_BRACE(), Text.translatable("meazy:parser.expected.end", "right_brace", "constructor_body"));
        return new ConstructorDeclarationStatement(modifiers, parameters, body);
    }
}
