package me.itzisonn_.meazy.parser.pasing_function.expression;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.parser.InvalidSyntaxException;
import me.itzisonn_.meazy.parser.ast.expression.CallExpression;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public class CallExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public CallExpressionParsingFunction() {
        super("call_expression");
    }

    @Override
    public Expression parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();

        Expression expression = parser.parseAfter(MeazyMain.getDefaultIdentifier("call_expression"), Expression.class);

        if (parser.getCurrent().getType().equals(TokenTypes.LEFT_PARENTHESIS())) {
            if (!(expression instanceof Identifier identifier)) {
                throw new InvalidSyntaxException(parser.getCurrent().getLine(), Text.translatable("meazy:parser.exception.call_not_identifier"));
            }

            List<Expression> args = ParsingHelper.parseArgs(context);
            return new CallExpression(identifier, args);
        }

        return expression;
    }
}
