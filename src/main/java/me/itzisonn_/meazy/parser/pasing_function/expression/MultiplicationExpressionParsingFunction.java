package me.itzisonn_.meazy.parser.pasing_function.expression;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lexer.TokenTypeSets;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class MultiplicationExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public MultiplicationExpressionParsingFunction() {
        super("multiplication_expression");
    }

    @Override
    public Expression parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();
        Expression left = parser.parseAfter(MeazyMain.getDefaultIdentifier("multiplication_expression"), Expression.class);

        while (TokenTypeSets.INSTANCE.getMultiplication().contains(parser.getCurrent().getType())) {
            String operator = parser.getCurrentAndNext().getValue();
            Expression right = parser.parseAfter(MeazyMain.getDefaultIdentifier("multiplication_expression"), Expression.class);
            left = new OperatorExpression(left, right, operator, OperatorType.INFIX);
        }

        return left;
    }
}
