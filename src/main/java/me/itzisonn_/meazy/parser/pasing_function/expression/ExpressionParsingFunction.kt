package me.itzisonn_.meazy.parser.pasing_function.expression;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class ExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public ExpressionParsingFunction() {
        super("expression");
    }

    @Override
    public Expression parse(ParsingContext context, @Nullable Object... extra) {
        return context.getParser().parseAfter(MeazyMain.getDefaultIdentifier("expression"), Expression.class);
    }
}
