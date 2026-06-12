package me.itzisonn_.meazy.parser.pasing_function.expression;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.operator.Operators;
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class PowerExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public PowerExpressionParsingFunction() {
        super("power_expression");
    }

    @Override
    public Expression parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();
        Expression left = parser.parseAfter(MeazyMain.getDefaultIdentifier("power_expression"), Expression.class);

        while (parser.getCurrent().getType().equals(TokenTypes.POWER())) {
            parser.next();
            Expression right = parser.parseAfter(MeazyMain.getDefaultIdentifier("power_expression"), Expression.class);
            left = new OperatorExpression(left, right, Operators.INSTANCE.getPower());
        }

        return left;
    }
}
