package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.parser.ast.statement.*;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public class InterfaceDeclarationStatementParsingFunction extends AbstractParsingFunction<InterfaceDeclarationStatement> {
    public InterfaceDeclarationStatementParsingFunction() {
        super("interface_declaration_statement");
    }

    @Override
    public InterfaceDeclarationStatement parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();
        Set<Modifier> modifiers = ParsingHelper.getModifiersFromExtra(extra);

        parser.getCurrentAndNext(TokenTypes.INTERFACE(), TextKt.translatable("meazy:parser.expected.keyword", "interface"));
        String id = parser.getCurrentAndNext(TokenTypes.ID(), TextKt.translatable("meazy:parser.expected.after_keyword", "id", "interface")).getValue();

        List<Statement> generatedBody = new ArrayList<>();

        Set<String> baseClasses = new HashSet<>();

        if (parser.getCurrent().getType().equals(TokenTypes.COLON())) {
            do {
                parser.next();
                baseClasses.add(parser.getCurrentAndNext(TokenTypes.ID(), TextKt.translatable("meazy:parser.expected", "id")).getValue());
            }
            while (parser.getCurrent().getType().equals(TokenTypes.COMMA()));
        }

        if (!parser.getCurrent().getType().equals(TokenTypes.LEFT_BRACE())) {
            return new InterfaceDeclarationStatement(modifiers, id, baseClasses, generatedBody);
        }

        parser.next(TokenTypes.LEFT_BRACE(), TextKt.translatable("meazy:parser.expected.start", "left_brace", "interface_body"));

        if (parser.getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) {
            parser.next();
            return new InterfaceDeclarationStatement(modifiers, id, baseClasses, generatedBody);
        }

        parser.getCurrentAndNext(TokenTypes.NEW_LINE(), TextKt.translatable("meazy:parser.expected", "new_line"));
        parser.moveOverOptionalNewLines();

        List<Statement> body = new ArrayList<>(generatedBody);
        while (!parser.getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !parser.getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) {
            Statement statement = parser.parse(MeazyMain.getDefaultIdentifier("interface_body_statement"), Statement.class);
            body.add(statement);
            parser.moveOverOptionalNewLines();
        }

        parser.next(TokenTypes.RIGHT_BRACE(), TextKt.translatable("meazy:parser.expected.end", "right_brace", "interface_body"));
        return new InterfaceDeclarationStatement(modifiers, id, baseClasses, body);
    }
}
