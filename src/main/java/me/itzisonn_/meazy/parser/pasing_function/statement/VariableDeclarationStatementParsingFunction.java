package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.InvalidSyntaxException;
import me.itzisonn_.meazy.parser.ast.expression.literal.NullLiteral;
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;

@NullMarked
public class VariableDeclarationStatementParsingFunction extends AbstractParsingFunction<VariableDeclarationStatement> {
    public VariableDeclarationStatementParsingFunction() {
        super("variable_declaration_statement");
    }

    @Override
    public VariableDeclarationStatement parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();
        Set<Modifier> modifiers = ParsingHelper.getModifiersFromExtra(extra);

        if (extra.length == 1) throw new IllegalArgumentException("Expected boolean as extra argument");
        if (!(extra[1] instanceof Boolean canWithoutValue)) throw new IllegalArgumentException("Expected boolean as extra argument");

        boolean isConstant = parser.getCurrentAndNext(TokenTypes.VARIABLE(), Text.translatable("meazy:parser.expected.keyword", "variable")).getValue().equals("val");

        String id = parser.getCurrentAndNext(TokenTypes.ID(), Text.translatable("meazy:parser.expected", "id")).getValue();

        DataType dataType = ParsingHelper.parseDataType(context);
        if (dataType == null) dataType = DataType.anyNullable();

        if (!parser.getCurrent().getType().equals(TokenTypes.ASSIGN())) {
            if (canWithoutValue) {
                return new VariableDeclarationStatement(modifiers, isConstant, id, dataType, null);
            }
            if (isConstant) throw new InvalidSyntaxException(parser.getCurrent().getLine(), Text.translatable("meazy:parser.exception.constant_without_value"));
            return new VariableDeclarationStatement(modifiers, false, id, dataType, new NullLiteral());
        }

        parser.next(TokenTypes.ASSIGN(), Text.translatable("meazy:parser.expected.after", "assign", "id"));

        return new VariableDeclarationStatement(modifiers, isConstant, id, dataType, parser.parse(MeazyMain.getDefaultIdentifier("expression"), Expression.class));
    }
}
