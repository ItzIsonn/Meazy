package me.itzisonn_.meazy.parser.pasing_function.expression;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.NullCheckExpression;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class NullCheckExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public NullCheckExpressionParsingFunction() {
        super("null_check_expression");
    }

    @Override
    public Expression parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();
        Expression checkExpression = parser.parseAfter(MeazyMain.getDefaultIdentifier("null_check_expression"), Expression.class);

        if (parser.getCurrent().getType().equals(TokenTypes.QUESTION_COLON())) {
            parser.next();
            Expression nullExpression = parser.parse(MeazyMain.getDefaultIdentifier("expression"), Expression.class);
            return new NullCheckExpression(checkExpression, nullExpression);
        }

        return checkExpression;
    }
}
