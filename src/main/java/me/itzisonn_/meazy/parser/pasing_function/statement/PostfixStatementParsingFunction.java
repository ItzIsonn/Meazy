package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.lexer.TokenTypeSets;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy.parser.ast.expression.literal.NumberLiteral;
import me.itzisonn_.meazy.parser.ast.statement.AssignmentStatement;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class PostfixStatementParsingFunction extends AbstractParsingFunction<Statement> {
    public PostfixStatementParsingFunction() {
        super("postfix_statement");
    }

    @Override
    public Statement parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();

        Expression left = parser.parse(MeazyMain.getDefaultIdentifier("expression"), Expression.class);
        Token token = parser.consume(TokenTypeSets.INSTANCE.getOperatorPostfix(), TextKt.translatable("meazy:parser.expected.end_statement", "operator_postfix", "postfix_statement"));

        Expression value = new OperatorExpression(
                left,
                new NumberLiteral("1"),
                token.getValue().substring(0, 1), OperatorType.INFIX
        );

        return new AssignmentStatement(left, value);
    }
}
