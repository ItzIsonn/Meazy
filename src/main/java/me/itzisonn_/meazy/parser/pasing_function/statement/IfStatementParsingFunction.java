package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.statement.IfStatement;
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class IfStatementParsingFunction extends AbstractParsingFunction<IfStatement> {
    public IfStatementParsingFunction() {
        super("if_statement");
    }

    @Override
    public IfStatement parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();

        parser.next(TokenTypes.IF(), TextKt.translatable("meazy:parser.expected.keyword", "if"));
        parser.next(TokenTypes.LEFT_PARENTHESIS(), TextKt.translatable("meazy:parser.expected.start", "left_parenthesis", "if_condition"));

        Expression condition = parser.parse(MeazyMain.getDefaultIdentifier("expression"), Expression.class);
        parser.next(TokenTypes.RIGHT_PARENTHESIS(), TextKt.translatable("meazy:parser.expected.end", "right_parenthesis", "if_condition"));

        List<LocalStatement> body = new ArrayList<>();
        if (parser.getCurrent().getType().equals(TokenTypes.LEFT_BRACE())) {
            parser.next();
            body = ParsingHelper.parseBody(context);
            parser.next(TokenTypes.RIGHT_BRACE(), TextKt.translatable("meazy:parser.expected.end", "right_brace", "if_body"));

        }
        else body.add(parser.parse(MeazyMain.getDefaultIdentifier("local_statement"), LocalStatement.class));

        int elsePos = parser.getPos() + 1;
        if (elsePos < parser.getSize() && parser.get(elsePos).getType().equals(TokenTypes.ELSE())) {
            parser.next(TokenTypes.NEW_LINE(), TextKt.translatable("meazy:parser.expected.end_statement", "new_line"));
        }

        IfStatement elseStatement = null;
        if (parser.getCurrent().getType().equals(TokenTypes.ELSE())) {
            parser.next();
            if (parser.getCurrent().getType().equals(TokenTypes.IF())) {
                elseStatement = parser.parse(MeazyMain.getDefaultIdentifier("if_statement"), IfStatement.class);
            }
            else {
                List<LocalStatement> elseBody = new ArrayList<>();
                if (parser.getCurrent().getType().equals(TokenTypes.LEFT_BRACE())) {
                    parser.next();
                    elseBody = ParsingHelper.parseBody(context);
                    parser.next(TokenTypes.RIGHT_BRACE(), TextKt.translatable("meazy:parser.expected.end", "right_brace", "if_body"));
                }
                else elseBody.add(parser.parse(MeazyMain.getDefaultIdentifier("local_statement"), LocalStatement.class));

                elseStatement = new IfStatement(null, elseBody, null);
            }
        }

        return new IfStatement(condition, body, elseStatement);
    }
}
