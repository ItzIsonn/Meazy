package me.itzisonn_.meazy.parser.pasing_function.expression;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.lexer.TokenType;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.literal.*;
import me.itzisonn_.meazy.parser.InvalidStatementException;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class PrimaryExpressionParsingFunction extends AbstractParsingFunction<Expression> {
    public PrimaryExpressionParsingFunction() {
        super("primary_expression");
    }

    @Override
    public Expression parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();
        Token token = parser.getCurrent();
        TokenType tokenType = token.getType();

        if (tokenType.equals(TokenTypes.ID())) {
            if (parser.getTokens().size() > parser.getPos() + 1 && parser.getTokens().get(parser.getPos() + 1).getType().equals(TokenTypes.LEFT_PARENTHESIS())) {
                String id = parser.getCurrentAndNext().getValue();
                if (Character.isUpperCase(id.charAt(0))) return new ClassIdentifier(id);
                else return new FunctionIdentifier(id);
            }

            if (parser.getPos() > 0 && parser.getTokens().get(parser.getPos() - 1).getType().equals(TokenTypes.DOT())) {
                return new VariableIdentifier(parser.getCurrentAndNext().getValue());
            }

            String id = parser.getCurrentAndNext().getValue();
            if (Character.isUpperCase(id.charAt(0))) return new ClassIdentifier(id);
            else return new VariableIdentifier(id);
        }
        if (tokenType.equals(TokenTypes.NULL())) {
            parser.getCurrentAndNext();
            return new NullLiteral();
        }
        if (tokenType.equals(TokenTypes.NUMBER())) return new NumberLiteral(parser.getCurrentAndNext().getValue());
        if (tokenType.equals(TokenTypes.STRING())) return new StringLiteral(ParsingHelper.parseString(context));
        if (tokenType.equals(TokenTypes.BOOLEAN())) return new BooleanLiteral(Boolean.parseBoolean(parser.getCurrentAndNext().getValue()));
        if (tokenType.equals(TokenTypes.THIS())) {
            parser.getCurrentAndNext();
            return new ThisLiteral();
        }
        if (tokenType.equals(TokenTypes.LEFT_PARENTHESIS())) {
            parser.getCurrentAndNext();
            Expression value = parser.parse(MeazyMain.getDefaultIdentifier("expression"), Expression.class);
            parser.getCurrentAndNext(TokenTypes.RIGHT_PARENTHESIS(), TextKt.translatable("meazy:parser.expected", "right_parenthesis"));
            return value;
        }

        throw new InvalidStatementException(token.getLine(), TextKt.translatable("meazy:parser.exception.cant_parse", tokenType.getId()));
    }
}
