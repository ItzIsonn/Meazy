package me.itzisonn_.meazy.parser.pasing_function.expression;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lexer.TokenTypeSets;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.UnexpectedTokenException;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.parser.ast.expression.MemberExpression;
import me.itzisonn_.meazy.parser.ast.expression.CallExpression;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class MemberExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public MemberExpressionParsingFunction() {
        super("member_expression");
    }

    @Override
    public Expression parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();
        Expression object = parser.parseAfter(MeazyMain.getDefaultIdentifier("member_expression"), Expression.class);

        while (TokenTypeSets.INSTANCE.getMemberAccess().contains(parser.getCurrent().getType())) {
            boolean isNullSafe = parser.consume().getType().equals(TokenTypes.QUESTION_DOT());
            Expression member = parser.parseAfter(MeazyMain.getDefaultIdentifier("member_expression"), Expression.class);

            if (!(member instanceof Identifier) && !(member instanceof CallExpression)) {
                throw new UnexpectedTokenException(parser.getCurrent().getLine(), TextKt.translatable("meazy:parser.exception.member_expression"));
            }

            object = new MemberExpression(object, member, isNullSafe);
        }

        return object;
    }
}
