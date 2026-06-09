package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.parser.ast.statement.*;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.InvalidStatementException;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;

@NullMarked
public class GlobalStatementParsingFunction extends AbstractParsingFunction<Statement> {
    public GlobalStatementParsingFunction() {
        super("global_statement");
    }

    @Override
    public Statement parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();
        Set<Modifier> modifiers = ParsingHelper.parseModifiers(context);

        if (parser.getCurrent().getType().equals(TokenTypes.CLASS())) {
            return parser.parse(MeazyMain.getDefaultIdentifier("class_declaration_statement"), ClassDeclarationStatement.class, modifiers);
        }
        if (parser.getCurrent().getType().equals(TokenTypes.INTERFACE())) {
            return parser.parse(MeazyMain.getDefaultIdentifier("interface_declaration_statement"), InterfaceDeclarationStatement.class, modifiers);
        }
        if (parser.getCurrent().getType().equals(TokenTypes.FUNCTION())) {
            return parser.parse(MeazyMain.getDefaultIdentifier("function_declaration_statement"), FunctionDeclarationStatement.class, modifiers, false);
        }
        if (parser.getCurrent().getType().equals(TokenTypes.VARIABLE())) {
            return parser.parse(MeazyMain.getDefaultIdentifier("variable_declaration_statement"), VariableDeclarationStatement.class, modifiers, false);
        }

        throw new InvalidStatementException(parser.getCurrent().getLine(), TextKt.translatable("meazy:parser.exception.global_statement"));
    }
}
