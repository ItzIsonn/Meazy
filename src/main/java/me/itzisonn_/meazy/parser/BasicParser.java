package me.itzisonn_.meazy.parser;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lexer.*;
import me.itzisonn_.meazy.parser.ast.*;
import me.itzisonn_.meazy.parser.ast.expression.*;
import me.itzisonn_.meazy.parser.ast.expression.call_expression.*;
import me.itzisonn_.meazy.parser.ast.expression.identifier.*;
import me.itzisonn_.meazy.parser.ast.expression.literal.*;
import me.itzisonn_.meazy.parser.ast.statement.*;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BasicParser implements Parser {
    private ArrayList<Token> tokens;
    private int pos = 0;

    protected Token getCurrent() {
        return tokens.get(pos);
    }

    protected Token getCurrentAndRemove() {
        Token token = getCurrent();
        pos++;
        return token;
    }

    protected Token getCurrentAndRemove(TokenType tokenType, String e) throws RuntimeException {
        if (!getCurrent().getType().equals(tokenType)) throw new UnexpectedTokenException(e, getCurrent().getLine());
        return getCurrentAndRemove();
    }

    protected void removeOptionalNewLine() {
        if (getCurrent().getType().equals(TokenTypes.NEW_LINE())) getCurrentAndRemove();
    }

    @Override
    public Program produceAST(ArrayList<Token> tokens) {
        if (tokens == null) throw new NullPointerException("Tokens can't be null!");
        this.tokens = tokens;

        removeOptionalNewLine();

        ArrayList<Statement> body = new ArrayList<>();
        while (!getCurrent().getType().equals(TokenTypes.END_OF_FILE())) {
            Statement statement = parseGlobalStatement();
            body.add(statement);
        }

        return new Program(MeazyMain.getVersion(), body);
    }

    protected Statement parseGlobalStatement() {
        if (getCurrent().getType().equals(TokenTypes.FUNCTION())) return parseFunctionDeclaration(new HashSet<>());
        if (getCurrent().getType().equals(TokenTypes.VARIABLE())) {
            VariableDeclarationStatement variableDeclarationStatement = parseVariableDeclaration(new HashSet<>(), false);
            getCurrentAndRemove(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the variable declaration");
            return variableDeclarationStatement;
        }
        if (getCurrent().getType().equals(TokenTypes.CLASS())) return parseClassDeclaration();
        throw new InvalidStatementException("At global environment you only can declare variable, function or class", getCurrent().getLine());
    }



    protected ClassDeclarationStatement parseClassDeclaration() {
        getCurrentAndRemove();
        String id = getCurrentAndRemove(TokenTypes.ID(), "Expected identifier after class keyword").getValue();

        removeOptionalNewLine();
        getCurrentAndRemove(TokenTypes.LEFT_BRACE(), "Expected left brace to open class body");
        removeOptionalNewLine();

        ArrayList<Statement> body = new ArrayList<>();
        while (!getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) {
            body.add(parseClassBodyStatement());
        }

        removeOptionalNewLine();
        getCurrentAndRemove(TokenTypes.RIGHT_BRACE(), "Expected right brace to close class body");
        getCurrentAndRemove(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the class declaration");

        return new ClassDeclarationStatement(id, body);
    }

    protected Statement parseClassBodyStatement() {
        Set<AccessModifier> accessModifiers = new HashSet<>();
        while (TokenTypeSets.ACCESS_MODIFIERS.contains(getCurrent().getType())) {
            TokenType current = getCurrentAndRemove().getType();
            if (current.equals(TokenTypes.PRIVATE())) accessModifiers.add(AccessModifiers.PRIVATE());
            if (current.equals(TokenTypes.SHARED())) accessModifiers.add(AccessModifiers.SHARED());
        }

        if (getCurrent().getType().equals(TokenTypes.FUNCTION())) return parseFunctionDeclaration(accessModifiers);
        if (getCurrent().getType().equals(TokenTypes.VARIABLE())) {
            VariableDeclarationStatement variableDeclarationStatement = parseVariableDeclaration(accessModifiers, true);
            getCurrentAndRemove(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the variable declaration");
            return variableDeclarationStatement;
        }
        if (getCurrent().getType().equals(TokenTypes.CONSTRUCTOR())) return parseConstructorDeclaration(accessModifiers);
        throw new InvalidStatementException("Invalid statement found", getCurrent().getLine());
    }



    protected FunctionDeclarationStatement parseFunctionDeclaration(Set<AccessModifier> accessModifiers) {
        getCurrentAndRemove();
        String id = getCurrentAndRemove(TokenTypes.ID(), "Expected identifier after function keyword").getValue();

        ArrayList<Expression> rawArgs = parseArgs();
        ArrayList<CallArgExpression> args = new ArrayList<>();
        for (Expression arg : rawArgs) {
            if (!(arg instanceof CallArgExpression callArgExpression)) throw new UnexpectedTokenException("Expected function args", getCurrent().getLine());
            args.add(callArgExpression);
        }

        DataType dataType = null;
        if (getCurrent().getType().equals(TokenTypes.COLON())) {
            getCurrentAndRemove();
            if (!getCurrent().getType().equals(TokenTypes.ID()))
                throw new InvalidStatementException("Must specify function's return data type after colon", getCurrent().getLine());

            dataType = DataTypes.parse(getCurrentAndRemove(TokenTypes.ID(), "Expected data type's id").getValue());
        }

        Expression arraySize = null;
        if (getCurrent().getType().equals(TokenTypes.LEFT_BRACKET())) {
            getCurrentAndRemove();
            if (getCurrent().getType().equals(TokenTypes.RIGHT_BRACKET())) arraySize = new NullLiteral();
            else {
                arraySize = parseExpression();
            }
            getCurrentAndRemove(TokenTypes.RIGHT_BRACKET(), "Expected right bracket to close array size");
        }

        removeOptionalNewLine();
        getCurrentAndRemove(TokenTypes.LEFT_BRACE(), "Expected left brace to open function body");
        removeOptionalNewLine();

        ArrayList<Statement> body = new ArrayList<>();
        while (!getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) {
            body.add(parseStatement());
        }

        removeOptionalNewLine();
        getCurrentAndRemove(TokenTypes.RIGHT_BRACE(), "Expected right brace to close function body");
        getCurrentAndRemove(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the function declaration");

        return new FunctionDeclarationStatement(id, args, body, dataType, arraySize, accessModifiers);
    }

    protected ArrayList<Expression> parseArgs() {
        getCurrentAndRemove(TokenTypes.LEFT_PAREN(), "Expected left parenthesis");
        ArrayList<Expression> args = getCurrent().getType() == TokenTypes.RIGHT_PAREN() ? new ArrayList<>() : parseArgsList();
        getCurrentAndRemove(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis");
        return args;
    }

    protected ArrayList<Expression> parseArgsList() {
        ArrayList<Expression> args = new ArrayList<>(List.of(parseFunctionArg()));

        while (getCurrent().getType().equals(TokenTypes.COMMA())) {
            getCurrentAndRemove();
            args.add(parseFunctionArg());
        }

        return args;
    }

    protected CallArgExpression parseFunctionArg() {
        if (!getCurrent().getType().equals(TokenTypes.VARIABLE()))
            throw new UnexpectedTokenException("Expected variable keyword at the beginning of function arg", getCurrent().getLine());
        boolean isConstant = getCurrentAndRemove().getValue().equals("val");
        String id = getCurrentAndRemove(TokenTypes.ID(), "Expected identifier after variable keyword in function arg").getValue();

        Expression arraySize = null;
        if (getCurrent().getType().equals(TokenTypes.LEFT_BRACKET())) {
            getCurrentAndRemove();
            if (getCurrent().getType().equals(TokenTypes.RIGHT_BRACKET())) arraySize = new NullLiteral();
            else {
                arraySize = parseExpression();
            }
            getCurrentAndRemove(TokenTypes.RIGHT_BRACKET(), "Expected right bracket to close array size");
        }

        DataType argDataType = null;
        if (getCurrent().getType().equals(TokenTypes.COLON())) {
            getCurrentAndRemove();
            if (!getCurrent().getType().equals(TokenTypes.ID())) throw new InvalidStatementException("Must specify arg's data type after colon", getCurrent().getLine());

            argDataType = DataTypes.parse(getCurrentAndRemove(TokenTypes.ID(), "Expected data type's id").getValue());
        }

        return new CallArgExpression(id, arraySize, argDataType, isConstant);
    }



    protected VariableDeclarationStatement parseVariableDeclaration(Set<AccessModifier> accessModifiers, boolean canWithoutValue) {
        boolean isConstant = getCurrentAndRemove().getValue().equals("val");
        String id = getCurrentAndRemove(TokenTypes.ID(), "Expected identifier after variable keyword").getValue();

        Expression arraySize = null;
        if (getCurrent().getType().equals(TokenTypes.LEFT_BRACKET())) {
            getCurrentAndRemove();
            if (getCurrent().getType().equals(TokenTypes.RIGHT_BRACKET())) arraySize = new NullLiteral();
            else {
                arraySize = parseExpression();
            }
            getCurrentAndRemove(TokenTypes.RIGHT_BRACKET(), "Expected right bracket to close array size");
        }

        DataType dataType = null;
        if (getCurrent().getType().equals(TokenTypes.COLON())) {
            getCurrentAndRemove();
            if (!getCurrent().getType().equals(TokenTypes.ID()))
                throw new InvalidStatementException("Must specify variable's data type after colon", getCurrent().getLine());

            dataType = DataTypes.parse(getCurrentAndRemove(TokenTypes.ID(), "Expected data type's id").getValue());
        }

        if (!getCurrent().getType().equals(TokenTypes.ASSIGN())) {
            if (canWithoutValue) {
                return new VariableDeclarationStatement(id, arraySize, dataType, null, isConstant, accessModifiers);
            }
            if (isConstant) throw new InvalidStatementException("Can't declare a constant variable without a value", getCurrent().getLine());
            return new VariableDeclarationStatement(id, arraySize, dataType, new NullLiteral(), false, accessModifiers);
        }

        getCurrentAndRemove(TokenTypes.ASSIGN(), "Expected ASSIGN token after the id in variable declaration");

        return new VariableDeclarationStatement(id, arraySize, dataType, parseExpression(), isConstant, accessModifiers);
    }



    protected ConstructorDeclarationStatement parseConstructorDeclaration(Set<AccessModifier> accessModifiers) {
        getCurrentAndRemove();

        ArrayList<Expression> rawArgs = parseArgs();
        ArrayList<CallArgExpression> args = new ArrayList<>();
        for (Expression arg : rawArgs) {
            if (!(arg instanceof CallArgExpression callArgExpression)) throw new UnexpectedTokenException("Expected constructor args", getCurrent().getLine());
            args.add(callArgExpression);
        }

        removeOptionalNewLine();
        getCurrentAndRemove(TokenTypes.LEFT_BRACE(), "Expected left brace to open constructor body");
        removeOptionalNewLine();

        ArrayList<Statement> body = new ArrayList<>();
        while (!getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) {
            body.add(parseStatement());
        }

        removeOptionalNewLine();
        getCurrentAndRemove(TokenTypes.RIGHT_BRACE(), "Expected right brace to close constructor body");
        getCurrentAndRemove(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the constructor declaration");

        return new ConstructorDeclarationStatement(args, body, accessModifiers);
    }



    protected Statement parseStatement() {
        if (getCurrent().getType().equals(TokenTypes.VARIABLE())) {
            VariableDeclarationStatement variableDeclarationStatement = parseVariableDeclaration(new HashSet<>(), false);
            getCurrentAndRemove(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the variable declaration");
            return variableDeclarationStatement;
        }
        if (getCurrent().getType().equals(TokenTypes.IF())) return parseIfStatement();
        if (getCurrent().getType().equals(TokenTypes.FOR())) return parseForStatement();
        if (getCurrent().getType().equals(TokenTypes.WHILE())) return parseWhileStatement();
        if (getCurrent().getType().equals(TokenTypes.RETURN())) return parseReturnStatement();
        if (getCurrent().getType().equals(TokenTypes.CONTINUE())) return parseContinueStatement();
        if (getCurrent().getType().equals(TokenTypes.BREAK())) return parseBreakStatement();

        if (getCurrent().getType().equals(TokenTypes.ID()) && tokens.size() > pos + 1 && tokens.get(pos + 1).getType().equals(TokenTypes.LEFT_PAREN())) {
            Expression callMemberExpression = parseCallExpression();
            if (callMemberExpression instanceof FunctionCallExpression) {
                getCurrentAndRemove(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the call statement");
                return callMemberExpression;
            }
        }

        if (getCurrent().getType().equals(TokenTypes.NEW())) {
            Expression classCallExpression = parseClassCallExpression();
            if (classCallExpression instanceof ClassCallExpression) {
                getCurrentAndRemove(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the class call statement");
                return classCallExpression;
            }
        }

        if (getCurrent().getType().equals(TokenTypes.ID())) {
            Expression assignmentExpression = parseAssignmentExpression();
            if (assignmentExpression instanceof AssignmentExpression || assignmentExpression instanceof MemberExpression) {
                getCurrentAndRemove(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the assignment statement");
                return assignmentExpression;
            }
        }

        throw new InvalidStatementException("Invalid statement found", getCurrent().getLine());
    }



    protected IfStatement parseIfStatement() {
        getCurrentAndRemove();

        getCurrentAndRemove(TokenTypes.LEFT_PAREN(), "Expected left parenthesis to open if condition");
        Expression condition = parseExpression();
        getCurrentAndRemove(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close if condition");

        ArrayList<Statement> body = new ArrayList<>();
        removeOptionalNewLine();
        if (getCurrent().getType().equals(TokenTypes.LEFT_BRACE())) {
            getCurrentAndRemove();
            body = parseBody();
            getCurrentAndRemove(TokenTypes.RIGHT_BRACE(), "Expected right brace to close if body");
            getCurrentAndRemove(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the if statement");
        }
        else body.add(parseStatement());

        IfStatement elseStatement = null;

        if (getCurrent().getType().equals(TokenTypes.ELSE())) {
            getCurrentAndRemove();
            if (getCurrent().getType().equals(TokenTypes.IF())) {
                elseStatement = parseIfStatement();
            }
            else {
                ArrayList<Statement> elseBody = new ArrayList<>();
                removeOptionalNewLine();
                if (getCurrent().getType().equals(TokenTypes.LEFT_BRACE())) {
                    getCurrentAndRemove();
                    elseBody = parseBody();
                    getCurrentAndRemove(TokenTypes.RIGHT_BRACE(), "Expected right brace to close if body");
                    getCurrentAndRemove(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the if statement");
                }
                else {
                    elseBody.add(parseStatement());
                }

                elseStatement = new IfStatement(null, elseBody, null);
            }
        }

        return new IfStatement(condition, body, elseStatement);
    }



    protected Statement parseForStatement() {
        getCurrentAndRemove();

        getCurrentAndRemove(TokenTypes.LEFT_PAREN(), "Expected left parenthesis to open for condition");

        if (getCurrent().getType().equals(TokenTypes.VARIABLE()) &&
                tokens.get(pos + 1) != null && tokens.get(pos + 1).getType().equals(TokenTypes.ID()) &&
                tokens.get(pos + 2) != null && (tokens.get(pos + 2).getType().equals(TokenTypes.IN()) ||
                (tokens.get(pos + 2).getType().equals(TokenTypes.COLON()) &&
                        tokens.get(pos + 3) != null && tokens.get(pos + 3).getType().equals(TokenTypes.ID()) &&
                        tokens.get(pos + 4) != null && tokens.get(pos + 4).getType().equals(TokenTypes.IN())
                ))) {
            VariableDeclarationStatement variableDeclarationStatement = parseVariableDeclaration(new HashSet<>(), true);
            getCurrentAndRemove(TokenTypes.IN(), "Expected IN after variable declaration");
            Expression collection = parseExpression();

            getCurrentAndRemove(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close for condition");

            removeOptionalNewLine();
            getCurrentAndRemove(TokenTypes.LEFT_BRACE(), "Expected left brace to open for body");
            ArrayList<Statement> body = parseBody();
            getCurrentAndRemove(TokenTypes.RIGHT_BRACE(), "Expected right brace to close for body");

            getCurrentAndRemove(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the for statement");

            return new ForeachStatement(variableDeclarationStatement, collection, body);
        }

        VariableDeclarationStatement variableDeclarationStatement = null;
        if (!getCurrent().getType().equals(TokenTypes.SEMICOLON())) {
            variableDeclarationStatement = parseVariableDeclaration(new HashSet<>(), false);
        }
        getCurrentAndRemove(TokenTypes.SEMICOLON(), "Expected semicolon as separator between for statement's args");

        Expression condition = null;
        if (!getCurrent().getType().equals(TokenTypes.SEMICOLON())) {
            condition = parseExpression();
        }
        getCurrentAndRemove(TokenTypes.SEMICOLON(), "Expected semicolon as separator between for statement's args");

        AssignmentExpression assignmentExpression = null;
        if (!getCurrent().getType().equals(TokenTypes.RIGHT_PAREN())) {
            if (parseAssignmentExpression() instanceof AssignmentExpression expression) {
                assignmentExpression = expression;
            }
        }
        getCurrentAndRemove(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close for condition");

        removeOptionalNewLine();
        getCurrentAndRemove(TokenTypes.LEFT_BRACE(), "Expected left brace to open for body");
        ArrayList<Statement> body = parseBody();
        getCurrentAndRemove(TokenTypes.RIGHT_BRACE(), "Expected right brace to close for body");

        getCurrentAndRemove(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the for statement");

        return new ForStatement(variableDeclarationStatement, condition, assignmentExpression, body);
    }



    protected WhileStatement parseWhileStatement() {
        getCurrentAndRemove();

        getCurrentAndRemove(TokenTypes.LEFT_PAREN(), "Expected left parenthesis to open while condition");
        Expression condition = parseExpression();
        getCurrentAndRemove(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close while condition");

        removeOptionalNewLine();
        getCurrentAndRemove(TokenTypes.LEFT_BRACE(), "Expected left brace to open while body");
        ArrayList<Statement> body = parseBody();
        getCurrentAndRemove(TokenTypes.RIGHT_BRACE(), "Expected right brace to close while body");

        getCurrentAndRemove(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the while statement");

        return new WhileStatement(condition, body);
    }

    protected ArrayList<Statement> parseBody() {
        ArrayList<Statement> body = new ArrayList<>();
        removeOptionalNewLine();

        while (!getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) {
            body.add(parseStatement());
        }

        removeOptionalNewLine();
        return body;
    }



    protected ReturnStatement parseReturnStatement() {
        getCurrentAndRemove();

        Expression expression = null;
        if (!getCurrent().getType().equals(TokenTypes.NEW_LINE())) {
            expression = parseExpression();
        }
        getCurrentAndRemove(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the return statement");

        return new ReturnStatement(expression);
    }

    protected ContinueStatement parseContinueStatement() {
        getCurrentAndRemove();
        getCurrentAndRemove(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the continue statement");
        return new ContinueStatement();
    }

    protected BreakStatement parseBreakStatement() {
        getCurrentAndRemove();
        getCurrentAndRemove(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the break statement");
        return new BreakStatement();
    }




    protected Expression parseExpression() {
        return parseAssignmentExpression();
    }

    protected Expression parseAssignmentExpression() {
        Expression left = parseArrayExpression();

        if (getCurrent().getType() == TokenTypes.ASSIGN()) {
            getCurrentAndRemove();
            Expression value = parseAssignmentExpression();
            return new AssignmentExpression(left, value);
        }
        else if (TokenTypeSets.OPERATOR_ASSIGN.contains(getCurrent().getType())) {
            Token token = getCurrentAndRemove();
            Expression value = new BinaryExpression(left, parseAssignmentExpression(), token.getValue().replaceAll("=$", ""));
            return new AssignmentExpression(left, value);
        }

        return left;
    }

    protected Expression parseArrayExpression() {
        if (getCurrent().getType().equals(TokenTypes.LEFT_BRACE())) {
            getCurrentAndRemove();
            ArrayList<Expression> args = getCurrent().getType() == TokenTypes.RIGHT_BRACE() ? new ArrayList<>() : parseCallArgsList();
            getCurrentAndRemove(TokenTypes.RIGHT_BRACE(), "Expected right brace to close array declaration");
            return new ArrayDeclarationExpression(args);
        }
        else return parseLogicalExpression();
    }

    protected Expression parseLogicalExpression() {
        Expression left = parseComparisonExpression();

        TokenType current = getCurrent().getType();
        while (current == TokenTypes.AND() || current == TokenTypes.OR()) {
            String operator = getCurrentAndRemove().getValue();
            Expression right = parseComparisonExpression();
            left = new LogicalExpression(left, right, operator);

            current = getCurrent().getType();
        }

        return left;
    }

    protected Expression parseComparisonExpression() {
        Expression left = parseAddExpression();

        TokenType current = getCurrent().getType();
        while (current == TokenTypes.EQUALS() || current == TokenTypes.NOT_EQUALS() || current == TokenTypes.GREATER() ||
                current == TokenTypes.GREATER_OR_EQUALS() || current == TokenTypes.LESS() || current == TokenTypes.LESS_OR_EQUALS()) {
            String operator = getCurrentAndRemove().getValue();
            Expression right = parseAddExpression();
            left = new ComparisonExpression(left, right, operator);

            current = getCurrent().getType();
        }

        return left;
    }

    protected Expression parseAddExpression() {
        Expression left = parseMultiplyExpression();

        while (getCurrent().getType() == TokenTypes.PLUS() || getCurrent().getType() == TokenTypes.MINUS()) {
            String operator = getCurrentAndRemove().getValue();
            Expression right = parseMultiplyExpression();
            left = new BinaryExpression(left, right, operator);
        }

        return left;
    }

    protected Expression parseMultiplyExpression() {
        Expression left = parsePowerExpression();

        while (getCurrent().getType() == TokenTypes.MULTIPLY() || getCurrent().getType() == TokenTypes.DIVIDE() || getCurrent().getType() == TokenTypes.PERCENT()) {
            String operator = getCurrentAndRemove().getValue();
            Expression right = parsePowerExpression();
            left = new BinaryExpression(left, right, operator);
        }

        return left;
    }

    protected Expression parsePowerExpression() {
        Expression left = parsePostfixExpression();

        while (getCurrent().getType() == TokenTypes.POWER()) {
            String operator = getCurrentAndRemove().getValue();
            Expression right = parsePostfixExpression();
            left = new BinaryExpression(left, right, operator);
        }

        return left;
    }

    protected Expression parsePostfixExpression() {
        Expression left = parseClassCallExpression();

        if (TokenTypeSets.OPERATOR_POSTFIX.contains(getCurrent().getType())) {
            Token token = getCurrentAndRemove();
            Expression value = new BinaryExpression(left, new NumberLiteral(1, true), token.getValue().substring(1));
            return new AssignmentExpression(left, value);
        }

        return left;
    }

    protected Expression parseClassCallExpression() {
        if (getCurrent().getType().equals(TokenTypes.NEW())) {
            getCurrentAndRemove();
            Expression expression = parseMemberExpression();
            if (expression instanceof CallExpression callExpression) {
                return new ClassCallExpression(callExpression.getCaller(), callExpression.getArgs());
            }
            if (expression instanceof MemberExpression memberExpression) {
                Expression member = memberExpression;
                while (member instanceof MemberExpression memberExpression1) {
                    if (!(memberExpression1.getObject() instanceof CallExpression callExpression)) member = memberExpression1.getObject();
                    else {
                        memberExpression1.setObject(new ClassCallExpression(callExpression.getCaller(), callExpression.getArgs()));
                        return memberExpression;
                    }
                }
            }
            throw new InvalidSyntaxException("Class creation must be call expression");
        }

        return parseMemberExpression();
    }

    protected Expression parseMemberExpression() {
        Expression object = parseCallExpression();

        while (getCurrent().getType() == TokenTypes.DOT()) {
            getCurrentAndRemove();
            Expression property = parseCallExpression();
            if (!(property instanceof Identifier) && !(property instanceof CallExpression) && getCurrent().getType() == TokenTypes.DOT())
                throw new UnexpectedTokenException("Right side must be either Identifier or Call", getCurrent().getLine());
            object = new MemberExpression(object, property);
        }

        return object;
    }

    protected Expression parseCallExpression() {
        Expression expression = parsePrimaryExpression();

        if (getCurrent().getType() == TokenTypes.LEFT_PAREN()) {
            return new FunctionCallExpression(expression, parseCallArgs());
        }

        return expression;
    }

    protected ArrayList<Expression> parseCallArgs() {
        getCurrentAndRemove(TokenTypes.LEFT_PAREN(), "Expected left parenthesis to open call args");
        ArrayList<Expression> args = getCurrent().getType() == TokenTypes.RIGHT_PAREN() ? new ArrayList<>() : parseCallArgsList();
        getCurrentAndRemove(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close call args");
        return args;
    }

    protected ArrayList<Expression> parseCallArgsList() {
        ArrayList<Expression> args = new ArrayList<>(List.of(parseExpression()));

        while (getCurrent().getType().equals(TokenTypes.COMMA())) {
            getCurrentAndRemove();
            args.add(parseExpression());
        }

        return args;
    }

    protected Expression parsePrimaryExpression() {
        TokenType tokenType = getCurrent().getType();

        if (tokenType == TokenTypes.ID()) {
            Identifier identifier;
            if ((pos != 0 && tokens.get(pos - 1).getType().equals(TokenTypes.NEW())) ||
                    (tokens.size() > pos + 1 && tokens.get(pos + 1).getType().equals(TokenTypes.DOT()) && pos != 0 && !tokens.get(pos - 1).getType().equals(TokenTypes.DOT())))
                identifier = new ClassIdentifier(getCurrentAndRemove().getValue());
            else if (tokens.size() > pos + 1 && tokens.get(pos + 1).getType().equals(TokenTypes.LEFT_PAREN())) {
                String value = getCurrentAndRemove().getValue();
                int currentPos = pos;
                ArrayList<Expression> args = parseCallArgs();
                pos = currentPos;
                identifier = new FunctionIdentifier(value, args);
            }
            else identifier = new VariableIdentifier(getCurrentAndRemove().getValue());

            if (getCurrent().getType().equals(TokenTypes.LEFT_BRACKET())) {
                getCurrentAndRemove();
                Expression arraySize = parseExpression();
                getCurrentAndRemove(TokenTypes.RIGHT_BRACKET(), "Expected right bracket to close array size");
                return new ArrayPointerExpression(identifier, arraySize);
            }

            return identifier;
        }
        if (tokenType == TokenTypes.NULL()) {
            getCurrentAndRemove();
            return new NullLiteral();
        }
        if (tokenType == TokenTypes.NUMBER()) {
            String value = getCurrentAndRemove().getValue();
            if (value.contains(".")) return new NumberLiteral(Double.parseDouble(value), false);
            else return new NumberLiteral(Double.parseDouble(value), true);
        }
        if (tokenType == TokenTypes.STRING()) {
            String value = getCurrentAndRemove().getValue();
            return new StringLiteral(value.substring(1, value.length() - 1));
        }
        if (tokenType == TokenTypes.LEFT_PAREN()) {
            getCurrentAndRemove();
            Expression value = parseExpression();
            getCurrentAndRemove(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis inside expression");
            return value;
        }
        if (tokenType == TokenTypes.BOOLEAN()) return new BooleanLiteral(Boolean.parseBoolean(getCurrentAndRemove().getValue()));
        if (tokenType == TokenTypes.NEW_LINE()) {
            getCurrentAndRemove();
            return null;
        }

        throw new UnsupportedTokenException(tokenType.getId());
    }
}