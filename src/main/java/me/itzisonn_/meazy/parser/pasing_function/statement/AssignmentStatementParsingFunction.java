package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.lexer.TokenTypeSets;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.UnexpectedTokenException;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.parser.ast.statement.AssignmentStatement;
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class AssignmentStatementParsingFunction extends AbstractParsingFunction<AssignmentStatement> {
    public AssignmentStatementParsingFunction() {
        super("assignment_statement");
    }

    @Override
    public AssignmentStatement parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();
        Expression left = parser.parse(MeazyMain.getDefaultIdentifier("expression"), Expression.class);

        if (parser.getCurrent().getType().equals(TokenTypes.ASSIGN())) {
            parser.next();
            Expression value = parser.parse(MeazyMain.getDefaultIdentifier("expression"), Expression.class);
            return new AssignmentStatement(left, value);
        }
        else if (TokenTypeSets.OPERATOR_ASSIGN().contains(parser.getCurrent().getType())) {
            Token token = parser.getCurrentAndNext();

            Expression value = new OperatorExpression(
                    left,
                    parser.parse(MeazyMain.getDefaultIdentifier("expression"), Expression.class),
                    token.getValue().replaceAll("=$", ""), OperatorType.INFIX
            );

            return new AssignmentStatement(left, value);
        }

        throw new UnexpectedTokenException(parser.getCurrent().getLine(), TextKt.literal("Expected assign operators")); //TODO
    }
}
