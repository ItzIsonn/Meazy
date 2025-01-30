package me.itzisonn_.meazy.parser;

import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.lexer.TokenType;
import me.itzisonn_.meazy.lexer.TokenTypeSets;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.ast.expression.*;
import me.itzisonn_.meazy.parser.ast.expression.call_expression.CallExpression;
import me.itzisonn_.meazy.parser.ast.expression.call_expression.ClassCallExpression;
import me.itzisonn_.meazy.parser.ast.expression.call_expression.FunctionCallExpression;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.literal.*;
import me.itzisonn_.meazy.parser.ast.statement.*;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryIdentifier;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static me.itzisonn_.meazy.parser.Parser.*;

/**
 * All basic ParsingFunctions
 *
 * @see Registries#PARSING_FUNCTIONS
 */
public final class ParsingFunctions {
    private static boolean isInit = false;

    private ParsingFunctions() {}



    /**
     * Initializes {@link Registries#PARSING_FUNCTIONS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#PARSING_FUNCTIONS} registry has already been initialized
     */
    public static void INIT() {
        if (isInit) throw new IllegalStateException("ParsingFunctions have already been initialized!");
        isInit = true;

        register("global_statement", extra -> {
            if (getCurrent().getType().equals(TokenTypes.FUNCTION())) {
                return parse(RegistryIdentifier.ofDefault("function_declaration"), FunctionDeclarationStatement.class, new HashSet<>());
            }
            if (getCurrent().getType().equals(TokenTypes.VARIABLE())) {
                VariableDeclarationStatement variableDeclarationStatement =
                        parse(RegistryIdentifier.ofDefault("variable_declaration"), VariableDeclarationStatement.class, new HashSet<>(), false);
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the variable declaration");
                return variableDeclarationStatement;
            }
            if (getCurrent().getType().equals(TokenTypes.CLASS())) {
                return parse(RegistryIdentifier.ofDefault("class_declaration"), ClassDeclarationStatement.class);
            }

            throw new InvalidStatementException("At global environment you only can declare variable, function or class", getCurrent().getLine());
        });

        register("class_declaration", extra -> {
                getCurrentAndNext();
                String id = getCurrentAndNext(TokenTypes.ID(), "Expected identifier after class keyword").getValue();

                moveOverOptionalNewLines();
                getCurrentAndNext(TokenTypes.LEFT_BRACE(), "Expected left brace to open class body");
                moveOverOptionalNewLines();

                List<Statement> body = new ArrayList<>();
                while (!getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) {
                    body.add(parse(RegistryIdentifier.ofDefault("class_body_statement")));
                }

                moveOverOptionalNewLines();
                getCurrentAndNext(TokenTypes.RIGHT_BRACE(), "Expected right brace to close class body");
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the class declaration");

                return new ClassDeclarationStatement(id, body);
        });

        register("class_body_statement", extra -> {
            Set<String> accessModifiers = new HashSet<>();
            while (TokenTypeSets.ACCESS_MODIFIERS().contains(getCurrent().getType())) {
                accessModifiers.add(getCurrentAndNext().getType().getId());
            }

            if (getCurrent().getType().equals(TokenTypes.FUNCTION())) {
                return parse(RegistryIdentifier.ofDefault("function_declaration"), FunctionDeclarationStatement.class, accessModifiers);
            }
            if (getCurrent().getType().equals(TokenTypes.VARIABLE())) {
                VariableDeclarationStatement variableDeclarationStatement =
                        parse(RegistryIdentifier.ofDefault("variable_declaration"), VariableDeclarationStatement.class, accessModifiers, true);
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the variable declaration");
                return variableDeclarationStatement;
            }
            if (getCurrent().getType().equals(TokenTypes.CONSTRUCTOR())) {
                return parse(RegistryIdentifier.ofDefault("constructor_declaration"), ConstructorDeclarationStatement.class, accessModifiers);
            }
            throw new InvalidStatementException("Invalid statement found", getCurrent().getLine());
        });

        register("function_declaration", extra -> {
            Set<String> accessModifiers = getAccessModifiers(extra);

            getCurrentAndNext();
            String id = getCurrentAndNext(TokenTypes.ID(), "Expected identifier after function keyword").getValue();

            List<CallArgExpression> args = parseArgs().stream().map(expression -> {
                if (!(expression instanceof CallArgExpression callArgExpression)) throw new UnexpectedTokenException("Expected function args", getCurrent().getLine());
                return callArgExpression;
            }).toList();

            String dataType = null;
            if (getCurrent().getType().equals(TokenTypes.COLON())) {
                getCurrentAndNext();
                if (!getCurrent().getType().equals(TokenTypes.ID()))
                    throw new InvalidStatementException("Must specify function's return data type after colon", getCurrent().getLine());

                dataType = getCurrentAndNext(TokenTypes.ID(), "Expected data type's id").getValue();
            }

            moveOverOptionalNewLines();
            getCurrentAndNext(TokenTypes.LEFT_BRACE(), "Expected left brace to open function body");
            List<Statement> body = parseBody();
            getCurrentAndNext(TokenTypes.RIGHT_BRACE(), "Expected right brace to close function body");
            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the function declaration");

            return new FunctionDeclarationStatement(accessModifiers, id, args, body, dataType);
        });

        register("function_arg", extra -> {
            if (!getCurrent().getType().equals(TokenTypes.VARIABLE()))
                throw new UnexpectedTokenException("Expected variable keyword at the beginning of function arg", getCurrent().getLine());
            boolean isConstant = getCurrentAndNext().getValue().equals("val");
            String id = getCurrentAndNext(TokenTypes.ID(), "Expected identifier after variable keyword in function arg").getValue();

            String argDataType = "Any";
            if (getCurrent().getType().equals(TokenTypes.COLON())) {
                getCurrentAndNext();
                if (!getCurrent().getType().equals(TokenTypes.ID())) throw new InvalidStatementException("Must specify arg's data type after colon", getCurrent().getLine());

                argDataType = getCurrentAndNext(TokenTypes.ID(), "Expected data type's id").getValue();
            }

            return new CallArgExpression(id, argDataType, isConstant);
        });

        register("variable_declaration", extra -> {
            Set<String> accessModifiers = getAccessModifiers(extra);

            if (extra.length == 1) throw new IllegalArgumentException("Expected boolean as extra argument");
            if (!(extra[1] instanceof Boolean canWithoutValue)) throw new IllegalArgumentException("Expected boolean as extra argument");

            boolean isConstant = getCurrentAndNext().getValue().equals("val");

            List<VariableDeclarationStatement.VariableDeclarationInfo> declarations = new ArrayList<>();
            declarations.add(parseVariableDeclarationInfo(isConstant, canWithoutValue));

            while (getCurrent().getType().equals(TokenTypes.COMMA())) {
                getCurrentAndNext();
                declarations.add(parseVariableDeclarationInfo(isConstant, canWithoutValue));
            }

            return new VariableDeclarationStatement(accessModifiers, isConstant, declarations);
        });

        register("constructor_declaration", extra -> {
            Set<String> accessModifiers = getAccessModifiers(extra);
            getCurrentAndNext();

            List<CallArgExpression> args = parseArgs().stream().map(expression -> {
                if (!(expression instanceof CallArgExpression callArgExpression)) throw new UnexpectedTokenException("Expected constructor args", getCurrent().getLine());
                return callArgExpression;
            }).toList();

            moveOverOptionalNewLines();
            getCurrentAndNext(TokenTypes.LEFT_BRACE(), "Expected left brace to open constructor body");
            List<Statement> body = parseBody();
            getCurrentAndNext(TokenTypes.RIGHT_BRACE(), "Expected right brace to close constructor body");
            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the constructor declaration");

            return new ConstructorDeclarationStatement(accessModifiers, args, body);
        });

        register("statement", extra -> {
            if (getCurrent().getType().equals(TokenTypes.VARIABLE())) {
                VariableDeclarationStatement variableDeclarationStatement =
                        parse(RegistryIdentifier.ofDefault("variable_declaration"), VariableDeclarationStatement.class, new HashSet<>(), false);
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the variable declaration");
                return variableDeclarationStatement;
            }
            if (getCurrent().getType().equals(TokenTypes.IF())) return parse(RegistryIdentifier.ofDefault("if_statement"));
            if (getCurrent().getType().equals(TokenTypes.FOR())) return parse(RegistryIdentifier.ofDefault("for_statement"));
            if (getCurrent().getType().equals(TokenTypes.WHILE())) return parse(RegistryIdentifier.ofDefault("while_statement"));
            if (getCurrent().getType().equals(TokenTypes.RETURN())) return parse(RegistryIdentifier.ofDefault("return_statement"));
            if (getCurrent().getType().equals(TokenTypes.CONTINUE())) return parse(RegistryIdentifier.ofDefault("continue_statement"));
            if (getCurrent().getType().equals(TokenTypes.BREAK())) return parse(RegistryIdentifier.ofDefault("break_statement"));

            Expression expression  = parse(RegistryIdentifier.ofDefault("expression"), Expression.class);
            if (expression instanceof FunctionCallExpression || expression instanceof ClassCallExpression ||
                    expression instanceof AssignmentExpression || expression instanceof MemberExpression) {
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of expression");
                return expression;
            }

            throw new InvalidStatementException("Invalid statement found", getCurrent().getLine());
        });

        register("if_statement", extra -> {
            getCurrentAndNext();

            getCurrentAndNext(TokenTypes.LEFT_PAREN(), "Expected left parenthesis to open if condition");
            Expression condition = parse(RegistryIdentifier.ofDefault("expression"), Expression.class);
            getCurrentAndNext(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close if condition");

            List<Statement> body = new ArrayList<>();
            moveOverOptionalNewLines();
            if (getCurrent().getType().equals(TokenTypes.LEFT_BRACE())) {
                getCurrentAndNext();
                body = parseBody();
                getCurrentAndNext(TokenTypes.RIGHT_BRACE(), "Expected right brace to close if body");
                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the if statement");
            }
            else body.add(parse(RegistryIdentifier.ofDefault("statement")));

            IfStatement elseStatement = null;

            if (getCurrent().getType().equals(TokenTypes.ELSE())) {
                getCurrentAndNext();
                if (getCurrent().getType().equals(TokenTypes.IF())) {
                    elseStatement = parse(RegistryIdentifier.ofDefault("if_statement"), IfStatement.class);
                }
                else {
                    List<Statement> elseBody = new ArrayList<>();
                    moveOverOptionalNewLines();
                    if (getCurrent().getType().equals(TokenTypes.LEFT_BRACE())) {
                        getCurrentAndNext();
                        elseBody = parseBody();
                        getCurrentAndNext(TokenTypes.RIGHT_BRACE(), "Expected right brace to close if body");
                        getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the if statement");
                    }
                    else {
                        elseBody.add(parse(RegistryIdentifier.ofDefault("statement")));
                    }

                    elseStatement = new IfStatement(null, elseBody, null);
                }
            }

            return new IfStatement(condition, body, elseStatement);
        });

        register("for_statement", extra -> {
            getCurrentAndNext();

            getCurrentAndNext(TokenTypes.LEFT_PAREN(), "Expected left parenthesis to open for condition");

            if (getCurrent().getType().equals(TokenTypes.VARIABLE()) &&
                    getTokens().get(getPos() + 1) != null && getTokens().get(getPos() + 1).getType().equals(TokenTypes.ID()) &&
                    getTokens().get(getPos() + 2) != null && (getTokens().get(getPos() + 2).getType().equals(TokenTypes.IN()) ||
                    (getTokens().get(getPos() + 2).getType().equals(TokenTypes.COLON()) &&
                            getTokens().get(getPos() + 3) != null && getTokens().get(getPos() + 3).getType().equals(TokenTypes.ID()) &&
                            getTokens().get(getPos() + 4) != null && getTokens().get(getPos() + 4).getType().equals(TokenTypes.IN())
                    ))) {
                VariableDeclarationStatement variableDeclarationStatement = parse(RegistryIdentifier.ofDefault("variable_declaration"), VariableDeclarationStatement.class, new HashSet<>(), true);
                if (variableDeclarationStatement.getDeclarationInfos().size() > 1) {
                    throw new InvalidSyntaxException("Foreach statement can declare only one variable");
                }
                getCurrentAndNext(TokenTypes.IN(), "Expected IN after variable declaration");
                Expression collection = parse(RegistryIdentifier.ofDefault("expression"), Expression.class);

                getCurrentAndNext(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close for condition");

                moveOverOptionalNewLines();
                getCurrentAndNext(TokenTypes.LEFT_BRACE(), "Expected left brace to open for body");
                List<Statement> body = parseBody();
                getCurrentAndNext(TokenTypes.RIGHT_BRACE(), "Expected right brace to close for body");

                getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the for statement");

                return new ForeachStatement(variableDeclarationStatement, collection, body);
            }

            VariableDeclarationStatement variableDeclarationStatement = null;
            if (!getCurrent().getType().equals(TokenTypes.SEMICOLON())) {
                variableDeclarationStatement = parse(RegistryIdentifier.ofDefault("variable_declaration"), VariableDeclarationStatement.class, new HashSet<>(), false);
            }
            getCurrentAndNext(TokenTypes.SEMICOLON(), "Expected semicolon as separator between for statement's args");

            Expression condition = null;
            if (!getCurrent().getType().equals(TokenTypes.SEMICOLON())) {
                condition = parse(RegistryIdentifier.ofDefault("expression"), Expression.class);
            }
            getCurrentAndNext(TokenTypes.SEMICOLON(), "Expected semicolon as separator between for statement's args");

            AssignmentExpression assignmentExpression = null;
            if (!getCurrent().getType().equals(TokenTypes.RIGHT_PAREN())) {
                if (parse(RegistryIdentifier.ofDefault("assignment_expression"), Expression.class) instanceof AssignmentExpression expression) {
                    assignmentExpression = expression;
                }
            }
            getCurrentAndNext(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close for condition");

            moveOverOptionalNewLines();
            getCurrentAndNext(TokenTypes.LEFT_BRACE(), "Expected left brace to open for body");
            List<Statement> body = parseBody();
            getCurrentAndNext(TokenTypes.RIGHT_BRACE(), "Expected right brace to close for body");

            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the for statement");

            return new ForStatement(variableDeclarationStatement, condition, assignmentExpression, body);
        });

        register("while_statement", extra -> {
            getCurrentAndNext();

            getCurrentAndNext(TokenTypes.LEFT_PAREN(), "Expected left parenthesis to open while condition");
            Expression condition = parse(RegistryIdentifier.ofDefault("expression"), Expression.class);
            getCurrentAndNext(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close while condition");

            moveOverOptionalNewLines();
            getCurrentAndNext(TokenTypes.LEFT_BRACE(), "Expected left brace to open while body");
            List<Statement> body = parseBody();
            getCurrentAndNext(TokenTypes.RIGHT_BRACE(), "Expected right brace to close while body");

            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the while statement");

            return new WhileStatement(condition, body);
        });

        register("return_statement", extra -> {
            getCurrentAndNext();

            Expression expression = null;
            if (!getCurrent().getType().equals(TokenTypes.NEW_LINE())) {
                expression = parse(RegistryIdentifier.ofDefault("expression"), Expression.class);
            }
            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the return statement");

            return new ReturnStatement(expression);
        });

        register("continue_statement", extra ->  {
            getCurrentAndNext();
            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the continue statement");
            return new ContinueStatement();
        });

        register("break_statement", extra ->  {
            getCurrentAndNext();
            getCurrentAndNext(TokenTypes.NEW_LINE(), "Expected NEW_LINE token in the end of the break statement");
            return new BreakStatement();
        });

        register("expression", extra -> parse(RegistryIdentifier.ofDefault("assignment_expression"), Expression.class));

        register("assignment_expression", extra -> {
            Expression left = parse(RegistryIdentifier.ofDefault("logical_expression"), Expression.class);

            if (getCurrent().getType() == TokenTypes.ASSIGN()) {
                getCurrentAndNext();
                Expression value = parse(RegistryIdentifier.ofDefault("assignment_expression"), Expression.class);
                return new AssignmentExpression(left, value);
            }
            else if (TokenTypeSets.OPERATOR_ASSIGN().contains(getCurrent().getType())) {
                Token token = getCurrentAndNext();
                Expression value = new BinaryExpression(
                        left,
                        parse(RegistryIdentifier.ofDefault("assignment_expression"), Expression.class),
                        token.getValue().replaceAll("=$", ""));
                return new AssignmentExpression(left, value);
            }

            return left;
        });

        register("logical_expression", extra -> {
            Expression left = parse(RegistryIdentifier.ofDefault("comparison_expression"), Expression.class);

            TokenType current = getCurrent().getType();
            while (current == TokenTypes.AND() || current == TokenTypes.OR()) {
                String operator = getCurrentAndNext().getValue();
                Expression right = parse(RegistryIdentifier.ofDefault("comparison_expression"), Expression.class);
                left = new LogicalExpression(left, right, operator);

                current = getCurrent().getType();
            }

            return left;
        });

        register("comparison_expression", extra -> {
            Expression left = parse(RegistryIdentifier.ofDefault("is_expression"), Expression.class);

            TokenType current = getCurrent().getType();
            while (current == TokenTypes.EQUALS() || current == TokenTypes.NOT_EQUALS() || current == TokenTypes.GREATER() ||
                    current == TokenTypes.GREATER_OR_EQUALS() || current == TokenTypes.LESS() || current == TokenTypes.LESS_OR_EQUALS()) {
                String operator = getCurrentAndNext().getValue();
                Expression right = parse(RegistryIdentifier.ofDefault("is_expression"), Expression.class);
                left = new ComparisonExpression(left, right, operator);

                current = getCurrent().getType();
            }

            return left;
        });

        register("is_expression", extra -> {
            Expression value = parse(RegistryIdentifier.ofDefault("addition_expression"), Expression.class);

            if (getCurrent().getType() == TokenTypes.IS()) {
                getCurrentAndNext();
                return new IsExpression(value, getCurrentAndNext(TokenTypes.ID(), "Must specify data type after is keyword").getValue());
            }

            return value;
        });

        register("addition_expression", extra -> {
            Expression left = parse(RegistryIdentifier.ofDefault("multiplication_expression"), Expression.class);

            while (getCurrent().getType() == TokenTypes.PLUS() || getCurrent().getType() == TokenTypes.MINUS()) {
                String operator = getCurrentAndNext().getValue();
                Expression right = parse(RegistryIdentifier.ofDefault("multiplication_expression"), Expression.class);
                left = new BinaryExpression(left, right, operator);
            }

            return left;
        });

        register("multiplication_expression", extra -> {
            Expression left = parse(RegistryIdentifier.ofDefault("power_expression"), Expression.class);

            while (getCurrent().getType() == TokenTypes.MULTIPLY() || getCurrent().getType() == TokenTypes.DIVIDE() || getCurrent().getType() == TokenTypes.PERCENT()) {
                String operator = getCurrentAndNext().getValue();
                Expression right = parse(RegistryIdentifier.ofDefault("power_expression"), Expression.class);
                left = new BinaryExpression(left, right, operator);
            }

            return left;
        });

        register("power_expression", extra -> {
            Expression left = parse(RegistryIdentifier.ofDefault("inversion_expression"), Expression.class);

            while (getCurrent().getType() == TokenTypes.POWER()) {
                String operator = getCurrentAndNext().getValue();
                Expression right = parse(RegistryIdentifier.ofDefault("inversion_expression"), Expression.class);
                left = new BinaryExpression(left, right, operator);
            }

            return left;
        });

        register("inversion_expression", extra -> {
            if (getCurrent().getType() == TokenTypes.INVERSION()) {
                getCurrentAndNext();
                return new InversionExpression(parse(RegistryIdentifier.ofDefault("postfix_expression"), Expression.class));
            }

            return parse(RegistryIdentifier.ofDefault("postfix_expression"), Expression.class);
        });

        register("postfix_expression", extra -> {
            Expression left = parse(RegistryIdentifier.ofDefault("class_call_expression"), Expression.class);

            if (TokenTypeSets.OPERATOR_POSTFIX().contains(getCurrent().getType())) {
                Token token = getCurrentAndNext();
                Expression value = new BinaryExpression(left, new NumberLiteral(1, true), token.getValue().substring(1));
                return new AssignmentExpression(left, value);
            }

            return left;
        });

        register("class_call_expression", extra -> {
            if (getCurrent().getType().equals(TokenTypes.NEW())) {
                getCurrentAndNext();
                Expression expression = parse(RegistryIdentifier.ofDefault("member_expression"), Expression.class);
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

            return parse(RegistryIdentifier.ofDefault("member_expression"), Expression.class);
        });

        register("member_expression", extra -> {
            Expression object = parse(RegistryIdentifier.ofDefault("call_expression"), Expression.class);

            while (getCurrent().getType() == TokenTypes.DOT()) {
                getCurrentAndNext();
                Expression property = parse(RegistryIdentifier.ofDefault("call_expression"), Expression.class);
                if (!(property instanceof Identifier) && !(property instanceof CallExpression) && getCurrent().getType() == TokenTypes.DOT())
                    throw new UnexpectedTokenException("Right side must be either Identifier or Call", getCurrent().getLine());
                object = new MemberExpression(object, property);
            }

            return object;
        });

        register("call_expression", extra -> {
            Expression expression = parse(RegistryIdentifier.ofDefault("primary_expression"), Expression.class);

            if (getCurrent().getType() == TokenTypes.LEFT_PAREN()) {
                getCurrentAndNext(TokenTypes.LEFT_PAREN(), "Expected left parenthesis to open call args");
                List<Expression> args = new ArrayList<>();
                if (getCurrent().getType() != TokenTypes.RIGHT_PAREN()) {
                    args.add(parse(RegistryIdentifier.ofDefault("expression"), Expression.class));

                    while (getCurrent().getType().equals(TokenTypes.COMMA())) {
                        getCurrentAndNext();
                        args.add(parse(RegistryIdentifier.ofDefault("expression"), Expression.class));
                    }
                }
                getCurrentAndNext(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis to close call args");
                return new FunctionCallExpression(expression, args);
            }

            return expression;
        });

        register("primary_expression", extra -> {
            TokenType tokenType = getCurrent().getType();

            if (tokenType == TokenTypes.ID()) {
                if ((getPos() != 0 && getTokens().get(getPos() - 1).getType().equals(TokenTypes.NEW())) ||
                        (getTokens().size() > getPos() + 1 && getTokens().get(getPos() + 1).getType().equals(TokenTypes.DOT()) && getPos() != 0 && !getTokens().get(getPos() - 1).getType().equals(TokenTypes.DOT())))
                    return new ClassIdentifier(getCurrentAndNext().getValue());
                else if (getTokens().size() > getPos() + 1 && getTokens().get(getPos() + 1).getType().equals(TokenTypes.LEFT_PAREN())) {
                    return new FunctionIdentifier(getCurrentAndNext().getValue());
                }
                else return new VariableIdentifier(getCurrentAndNext().getValue());
            }
            if (tokenType == TokenTypes.NULL()) {
                getCurrentAndNext();
                return new NullLiteral();
            }
            if (tokenType == TokenTypes.NUMBER()) {
                String value = getCurrentAndNext().getValue();
                if (value.contains(".")) return new NumberLiteral(Double.parseDouble(value), false);
                else return new NumberLiteral(Double.parseDouble(value), true);
            }
            if (tokenType == TokenTypes.STRING()) {
                String value = getCurrentAndNext().getValue();
                return new StringLiteral(value.substring(1, value.length() - 1));
            }
            if (tokenType == TokenTypes.BOOLEAN()) return new BooleanLiteral(Boolean.parseBoolean(getCurrentAndNext().getValue()));
            if (tokenType == TokenTypes.THIS()) {
                getCurrentAndNext();
                return new ThisLiteral();
            }
            if (tokenType == TokenTypes.LEFT_PAREN()) {
                getCurrentAndNext();
                Expression value = parse(RegistryIdentifier.ofDefault("expression"), Expression.class);
                getCurrentAndNext(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis inside expression");
                return value;
            }
            if (tokenType == TokenTypes.NEW_LINE()) {
                getCurrentAndNext();
                return null;
            }

            throw new InvalidStatementException("Can't parse token with type " + tokenType.getId());
        });
    }



    @SuppressWarnings("unchecked")
    private static Set<String> getAccessModifiers(Object[] extra) {
        if (extra.length == 0) throw new IllegalArgumentException("Expected Set of access modifiers as extra argument");
        if (!(extra[0] instanceof Set<?> set)) throw new IllegalArgumentException("Expected Set of access modifiers as extra argument");
        Set<String> accessModifiers;
        try {
            accessModifiers = (Set<String>) set;
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("Expected Set of access modifiers as extra argument");
        }
        return accessModifiers;
    }

    private static List<Expression> parseArgs() {
        getCurrentAndNext(TokenTypes.LEFT_PAREN(), "Expected left parenthesis");
        List<Expression> args = new ArrayList<>();
        if (!getCurrent().getType().equals(TokenTypes.RIGHT_PAREN())) {
            args.add(parse(RegistryIdentifier.ofDefault("function_arg"), Expression.class));

            while (getCurrent().getType().equals(TokenTypes.COMMA())) {
                getCurrentAndNext();
                args.add(parse(RegistryIdentifier.ofDefault("function_arg"), Expression.class));
            }
        }
        getCurrentAndNext(TokenTypes.RIGHT_PAREN(), "Expected right parenthesis");
        return args;
    }

    private static List<Statement> parseBody() {
        List<Statement> body = new ArrayList<>();
        moveOverOptionalNewLines();

        while (!getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) {
            body.add(parse(RegistryIdentifier.ofDefault("statement")));
            moveOverOptionalNewLines();
        }

        moveOverOptionalNewLines();
        return body;
    }

    private static VariableDeclarationStatement.VariableDeclarationInfo parseVariableDeclarationInfo(boolean isConstant, boolean canWithoutValue) {
        String id = getCurrentAndNext(TokenTypes.ID(), "Expected identifier in variable declaration statement").getValue();

        String dataType = "Any";
        if (getCurrent().getType().equals(TokenTypes.COLON())) {
            getCurrentAndNext();
            dataType = getCurrentAndNext(TokenTypes.ID(), "Must specify variable's data type after colon").getValue();
        }

        if (!getCurrent().getType().equals(TokenTypes.ASSIGN())) {
            if (canWithoutValue) {
                return new VariableDeclarationStatement.VariableDeclarationInfo(id, dataType, null);
            }
            if (isConstant) throw new InvalidStatementException("Can't declare a constant variable without a value", getCurrent().getLine());
            return new VariableDeclarationStatement.VariableDeclarationInfo(id, dataType, new NullLiteral());
        }

        getCurrentAndNext(TokenTypes.ASSIGN(), "Expected ASSIGN token after the id in variable declaration");

        return new VariableDeclarationStatement.VariableDeclarationInfo(id, dataType, parse(RegistryIdentifier.ofDefault("expression"), Expression.class));
    }



    private static void register(String id, ParsingFunction<? extends Statement> parsingFunction) {
        Registries.PARSING_FUNCTIONS.register(RegistryIdentifier.ofDefault(id), parsingFunction);
    }
}