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
public class NegationExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public NegationExpressionParsingFunction() {
        super("negation_expression");
    }

    @Override
    public Expression parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();

        if (parser.getCurrent().getType().equals(TokenTypes.MINUS())) {
            parser.next();
            Expression expression = parser.parseAfter(MeazyMain.getDefaultIdentifier("negation_expression"), Expression.class);
            return new OperatorExpression(expression, null, Operators.NEGATION());
        }

        return parser.parseAfter(MeazyMain.getDefaultIdentifier("negation_expression"), Expression.class);
    }
}
