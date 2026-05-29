package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lexer.TokenTypeSets;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.InvalidStatementException;
import me.itzisonn_.meazy.parser.InvalidSyntaxException;
import me.itzisonn_.meazy.parser.ast.expression.MemberExpression;
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy.parser.ast.expression.CallExpression;
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;

@NullMarked
public class StatementParsingFunction extends AbstractParsingFunction<Statement> {
    public StatementParsingFunction() {
        super("statement");
    }

    @Override
    public Statement parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();

        Set<Modifier> modifiers = ParsingHelper.parseModifiers(context);

        if (parser.getCurrent().getType().equals(TokenTypes.VARIABLE())) {
            return parser.parse(MeazyMain.getDefaultIdentifier("variable_declaration_statement"), VariableDeclarationStatement.class, modifiers, false);
        }
        if (!modifiers.isEmpty()) throw new InvalidSyntaxException(parser.getCurrent().getLine(), Text.translatable("meazy:parser.modifier.unexpected"));

        if (parser.getCurrent().getType().equals(TokenTypes.IF())) return parser.parse(MeazyMain.getDefaultIdentifier("if_statement"));
        if (parser.getCurrent().getType().equals(TokenTypes.FOR())) return parser.parse(MeazyMain.getDefaultIdentifier("foreach_statement"));
        if (parser.getCurrent().getType().equals(TokenTypes.WHILE())) return parser.parse(MeazyMain.getDefaultIdentifier("while_statement"));
        if (parser.getCurrent().getType().equals(TokenTypes.RETURN())) return parser.parse(MeazyMain.getDefaultIdentifier("return_statement"));
        if (parser.getCurrent().getType().equals(TokenTypes.CONTINUE())) return parser.parse(MeazyMain.getDefaultIdentifier("continue_statement"));
        if (parser.getCurrent().getType().equals(TokenTypes.BREAK())) return parser.parse(MeazyMain.getDefaultIdentifier("break_statement"));

        if (parser.currentLineHasToken(TokenTypes.ASSIGN()) || parser.currentLineHasToken(TokenTypeSets.OPERATOR_ASSIGN())) {
            return parser.parse(MeazyMain.getDefaultIdentifier("assignment_statement"));
        }

        if (parser.currentLineHasToken(TokenTypeSets.OPERATOR_POSTFIX())) {
            return parser.parse(MeazyMain.getDefaultIdentifier("postfix_statement"));
        }

        Expression expression = parser.parse(MeazyMain.getDefaultIdentifier("expression"), Expression.class);
        if (expression instanceof CallExpression || expression instanceof MemberExpression) {
            return expression;
        }
        if (expression instanceof OperatorExpression operatorExpression && operatorExpression.getOperator().getOperatorType() != OperatorType.INFIX) {
            return expression;
        }

        throw new InvalidStatementException(parser.getCurrent().getLine(), Text.translatable("meazy:parser.exception.statement"));
    }
}
