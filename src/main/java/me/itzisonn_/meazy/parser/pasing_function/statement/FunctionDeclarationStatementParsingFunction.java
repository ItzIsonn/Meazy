package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement;
import me.itzisonn_.meazy.parser.ast.statement.ReturnStatement;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.Parameter;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NullMarked
public class FunctionDeclarationStatementParsingFunction extends AbstractParsingFunction<FunctionDeclarationStatement> {
    public FunctionDeclarationStatementParsingFunction() {
        super("function_declaration_statement");
    }

    @Override
    public FunctionDeclarationStatement parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();
        Set<Modifier> modifiers = ParsingHelper.getModifiersFromExtra(extra);

        if (extra.length == 1) throw new IllegalArgumentException("Expected boolean as extra argument");
        if (!(extra[1] instanceof Boolean canBeAbstractWithoutModifier)) throw new IllegalArgumentException("Expected boolean as extra argument");

        parser.next(TokenTypes.FUNCTION(), TextKt.translatable("meazy:parser.expected.keyword", "function"));

        String classId = null;
        String id = parser.getCurrentAndNext(TokenTypes.ID(), TextKt.translatable("meazy:parser.expected.after_keyword", "id", "function")).getValue();
        if (parser.getCurrent().getType().equals(TokenTypes.DOT())) {
            parser.next();
            classId = id;
            id = parser.getCurrentAndNext(TokenTypes.ID(), TextKt.translatable("meazy:parser.expected", "id")).getValue();
        }

        List<Parameter> parameters = ParsingHelper.parseParameters(context);
        DataType dataType = ParsingHelper.parseDataType(context);

        if (modifiers.contains(Modifiers.ABSTRACT()) || (canBeAbstractWithoutModifier && parser.getCurrent().getType().equals(TokenTypes.NEW_LINE()))) {
            modifiers.add(Modifiers.ABSTRACT());
            return new FunctionDeclarationStatement(modifiers, id, parameters, new ArrayList<>(), dataType);
        }

        List<LocalStatement> body;
        Expression returnDataTypeValue;

        if (parser.getCurrent().getType().equals(TokenTypes.ASSIGN())) {
            parser.next();
            Expression expression = parser.parse(MeazyMain.getDefaultIdentifier("expression"), Expression.class);
            body = new ArrayList<>(List.<LocalStatement>of(new ReturnStatement(expression)));
            returnDataTypeValue = expression;
        }
        else {
            parser.next(TokenTypes.LEFT_BRACE(), TextKt.translatable("meazy:parser.expected.start", "left_brace", "function_body"));
            body = ParsingHelper.parseBody(context);
            parser.next(TokenTypes.RIGHT_BRACE(), TextKt.translatable("meazy:parser.expected.end", "right_brace", "function_body"));
            returnDataTypeValue = null;
        }

        return new FunctionDeclarationStatement(modifiers, id, classId, parameters, body, dataType, returnDataTypeValue);
    }
}
