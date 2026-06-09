package me.itzisonn_.meazy.parser.pasing_function.expression;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.collection_creation.MapCreationExpression;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@NullMarked
public class MapCreationExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public MapCreationExpressionParsingFunction() {
        super("map_creation_expression");
    }

    @Override
    public Expression parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();

        if (parser.getCurrent().getType().equals(TokenTypes.LEFT_BRACE())) {
            parser.getCurrentAndNext();
            Map<Expression, Expression> map = new HashMap<>();

            while (!parser.getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) {
                Expression key = parser.parse(MeazyMain.getDefaultIdentifier("list_creation_expression"), Expression.class);
                parser.getCurrentAndNext(TokenTypes.ASSIGN(), TextKt.literal("Expected assign TODO")); //TODO

                Expression value = parser.parse(MeazyMain.getDefaultIdentifier("expression"), Expression.class);
                map.put(key, value);

                if (!parser.getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) {
                    parser.getCurrentAndNext(TokenTypes.COMMA(), TextKt.translatable("meazy:parser.expected.separator_expression", "comma", "map_creation"));
                }
            }

            parser.getCurrentAndNext(TokenTypes.RIGHT_BRACE(), TextKt.translatable("meazy:parser.expected.end_expression", "right_brace", "map_creation"));
            return new MapCreationExpression(map);
        }

        return parser.parseAfter(MeazyMain.getDefaultIdentifier("map_creation_expression"), Expression.class);
    }
}
