package me.itzisonn_.meazy.parser.pasing_function.expression;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.IsExpression;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class IsExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public IsExpressionParsingFunction() {
        super("is_expression");
    }

    @Override
    public Expression parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();
        Expression value = parser.parseAfter(MeazyMain.getDefaultIdentifier("is_expression"), Expression.class);

        if (parser.getCurrent().getType().equals(TokenTypes.IS())) {
            boolean isLike = parser.consume().getValue().equals("islike");
            String id = parser.consume(TokenTypes.ID(), TextKt.translatable("meazy:parser.expected.after_keyword", "id", "is")).getValue();
            return new IsExpression(value, id, isLike);
        }

        return value;
    }
}
