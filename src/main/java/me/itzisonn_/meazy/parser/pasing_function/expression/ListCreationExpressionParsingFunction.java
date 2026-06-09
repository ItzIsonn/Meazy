package me.itzisonn_.meazy.parser.pasing_function.expression;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.collection_creation.ListCreationExpression;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class ListCreationExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public ListCreationExpressionParsingFunction() {
        super("list_creation_expression");
    }

    @Override
    public Expression parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();

        if (parser.getCurrent().getType().equals(TokenTypes.LEFT_BRACKET())) {
            parser.next();
            List<Expression> list = new ArrayList<>();

            while (!parser.getCurrent().getType().equals(TokenTypes.RIGHT_BRACKET())) {
                list.add(parser.parse(MeazyMain.getDefaultIdentifier("expression"), Expression.class));

                if (!parser.getCurrent().getType().equals(TokenTypes.RIGHT_BRACKET())) {
                    parser.getCurrentAndNext(TokenTypes.COMMA(), TextKt.translatable("meazy:parser.expected.separator_expression", "comma", "list_creation"));
                }
            }

            parser.getCurrentAndNext(TokenTypes.RIGHT_BRACKET(), TextKt.translatable("meazy:parser.expected.end_expression", "right_bracket", "list_creation"));

            return new ListCreationExpression(list);
        }

        return parser.parseAfter(MeazyMain.getDefaultIdentifier("list_creation_expression"), Expression.class);
    }
}
