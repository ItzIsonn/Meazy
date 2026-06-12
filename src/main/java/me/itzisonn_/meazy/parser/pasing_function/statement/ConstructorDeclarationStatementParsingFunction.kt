package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.Parameter;
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
        parser.next(TokenTypes.CONSTRUCTOR(), TextKt.translatable("meazy:parser.expected.keyword", "constructor"));

        List<Parameter> parameters = ParsingHelper.parseParameters(context);

        if (!parser.getCurrent().getType().equals(TokenTypes.LEFT_BRACE())) {
            return new ConstructorDeclarationStatement(modifiers, parameters, new ArrayList<>());
        }

        parser.next(TokenTypes.LEFT_BRACE(), TextKt.translatable("meazy:parser.expected.start", "left_brace", "constructor_body"));
        List<LocalStatement> body = ParsingHelper.parseBody(context);
        parser.next(TokenTypes.RIGHT_BRACE(), TextKt.translatable("meazy:parser.expected.end", "right_brace", "constructor_body"));

        return new ConstructorDeclarationStatement(modifiers, parameters, body);
    }
}
