package me.itzisonn_.meazy.parser.pasing_function.statement;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.parser.ParsingContext;
import me.itzisonn_.meazy.lexer.TokenTypes;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.Parameter;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.statement.*;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.parser.operator.OperatorType;
import me.itzisonn_.meazy.util.MiscUtils;
import me.itzisonn_.meazy.parser.InvalidSyntaxException;
import me.itzisonn_.meazy.parser.ast.expression.CallExpression;
import me.itzisonn_.meazy.parser.ast.expression.IsExpression;
import me.itzisonn_.meazy.parser.ast.expression.MemberExpression;
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.literal.BooleanLiteral;
import me.itzisonn_.meazy.parser.ast.expression.literal.NullLiteral;
import me.itzisonn_.meazy.parser.ast.expression.literal.StringLiteral;
import me.itzisonn_.meazy.parser.ast.expression.literal.ThisLiteral;
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction;
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.util.*;

@NullMarked
public class ClassDeclarationStatementParsingFunction extends AbstractParsingFunction<ClassDeclarationStatement> {
    public ClassDeclarationStatementParsingFunction() {
        super("class_declaration_statement");
    }

    @Override
    public ClassDeclarationStatement parse(ParsingContext context, @Nullable Object... extra) {
        Parser parser = context.getParser();
        Set<Modifier> modifiers = ParsingHelper.getModifiersFromExtra(extra);

        parser.consume(TokenTypes.CLASS(), TextKt.translatable("meazy:parser.expected.keyword", "class"));
        String id = parser.consume(TokenTypes.ID(), TextKt.translatable("meazy:parser.expected.after_keyword", "id", "class")).getValue();

        List<Statement> generatedBody = new ArrayList<>();
        if (modifiers.contains(Modifiers.INSTANCE.getData())) {
            generatedBody.addAll(generateDataBody(id, ParsingHelper.parseParameters(context)));
            modifiers.remove(Modifiers.INSTANCE.getData());
        }

        Set<String> baseClasses = new HashSet<>();
        int baseClassesLineNumber = -1;

        if (parser.getCurrent().getType().equals(TokenTypes.COLON())) {
            baseClassesLineNumber = parser.getCurrent().getLine();
            do {
                parser.next();
                baseClasses.add(parser.consume(TokenTypes.ID(), TextKt.translatable("meazy:parser.expected", "id")).getValue());
            }
            while (parser.getCurrent().getType().equals(TokenTypes.COMMA()));
        }

        if (!parser.getCurrent().getType().equals(TokenTypes.LEFT_BRACE())) {
            return new ClassDeclarationStatement(modifiers, id, baseClasses, generatedBody);
        }

        parser.next(TokenTypes.LEFT_BRACE(), TextKt.translatable("meazy:parser.expected.start", "left_brace", "class_body"));

        if (parser.getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) {
            parser.next();
            return new ClassDeclarationStatement(modifiers, id, baseClasses, generatedBody);
        }

        parser.consume(TokenTypes.NEW_LINE(), TextKt.translatable("meazy:parser.expected", "new_line"));
        parser.skipNewLines();

        LinkedHashMap<String, List<Expression>> enumIds = new LinkedHashMap<>();
        if (modifiers.contains(Modifiers.INSTANCE.getEnum())) {
            if (!baseClasses.isEmpty()) throw new InvalidSyntaxException(baseClassesLineNumber, TextKt.translatable("meazy:parser.exception.enums.base_classes"));

            String enumId = parser.consume(TokenTypes.ID(), TextKt.translatable("meazy:parser.expected", "id")).getValue();
            List<Expression> args;
            if (parser.getCurrent().getType().equals(TokenTypes.LEFT_PARENTHESIS())) args = ParsingHelper.parseArgs(context);
            else args = new ArrayList<>();
            enumIds.put(enumId, args);

            while (parser.getCurrent().getType().equals(TokenTypes.COMMA())) {
                parser.next();
                parser.skipNewLines();

                int lineNumber = parser.getCurrent().getLine();
                enumId = parser.consume(TokenTypes.ID(), TextKt.translatable("meazy:parser.expected", "id")).getValue();
                if (enumIds.containsKey(enumId)) throw new InvalidSyntaxException(lineNumber, TextKt.translatable("meazy:parser.exception.enums.duplicated_entries"));

                if (parser.getCurrent().getType().equals(TokenTypes.LEFT_PARENTHESIS())) args = ParsingHelper.parseArgs(context);
                else args = new ArrayList<>();
                enumIds.put(enumId, args);
            }

            parser.skipNewLines();
        }

        List<Statement> body = new ArrayList<>(generatedBody);
        while (!parser.getCurrent().getType().equals(TokenTypes.END_OF_FILE()) && !parser.getCurrent().getType().equals(TokenTypes.RIGHT_BRACE())) {
            Statement statement = parser.parse(MeazyMain.getDefaultIdentifier("class_body_statement"), Statement.class);
            body.add(statement);

            if (statement instanceof VariableDeclarationStatement variableDeclarationStatement) {
                if (variableDeclarationStatement.getModifiers().contains(Modifiers.INSTANCE.getGet())) {
                    body.add(getGetFunction(variableDeclarationStatement.getId(), variableDeclarationStatement.getDataType()));
                    variableDeclarationStatement.getModifiers().remove(Modifiers.INSTANCE.getGet());
                }
                if (variableDeclarationStatement.getModifiers().contains(Modifiers.INSTANCE.getSet()) && !variableDeclarationStatement.isConstant()) {
                    body.add(getSetFunction(variableDeclarationStatement.getId(), variableDeclarationStatement.getDataType()));
                    variableDeclarationStatement.getModifiers().remove(Modifiers.INSTANCE.getSet());
                }
            }

            parser.skipNewLines();
        }

        parser.next(TokenTypes.RIGHT_BRACE(), TextKt.translatable("meazy:parser.expected.end", "right_brace", "class_body"));
        return new ClassDeclarationStatement(modifiers, id, baseClasses, body, enumIds);
    }



    private static List<Statement> generateDataBody(String id, List<Parameter> dataVariables) {
        List<Statement> body = new ArrayList<>();

        for (Parameter dataVariable : dataVariables) {
            body.add(new VariableDeclarationStatement(
                    Set.of(Modifiers.INSTANCE.getPrivate()),
                    dataVariable.isConstant(),
                    dataVariable.getId(),
                    dataVariable.getDataType(),
                    null
            ));
        }

        List<LocalStatement> constructorBody = new ArrayList<>();
        for (Parameter callArgExpression : dataVariables) {
            constructorBody.add(new AssignmentStatement(new MemberExpression(new ThisLiteral(), new VariableIdentifier(callArgExpression.getId()), false), new VariableIdentifier(callArgExpression.getId())));
        }
        body.add(new ConstructorDeclarationStatement(Set.of(), dataVariables, constructorBody));

        for (Parameter dataVariable : dataVariables) {
            body.add(getGetFunction(dataVariable.getId(), dataVariable.getDataType()));
        }

        for (Parameter dataVariable : dataVariables) {
            if (dataVariable.isConstant()) continue;
            body.add(getSetFunction(dataVariable.getId(), dataVariable.getDataType()));
        }

        Expression toStringExpression = getToStringExpression(id, dataVariables);
        body.add(new FunctionDeclarationStatement(
                Set.of(),
                "toString",
                List.of(),
                List.of(new ReturnStatement(toStringExpression)),
                DataType.Companion.ofNonNull(ConstantDescs.CD_String)));

        List<Expression> copyArgs = new ArrayList<>();
        for (Parameter dataVariable : dataVariables) {
            copyArgs.add(new VariableIdentifier(dataVariable.getId()));
        }
        body.add(new FunctionDeclarationStatement(
                Set.of(),
                "copy",
                List.of(),
                List.of(new ReturnStatement(new CallExpression(new ClassIdentifier(id), copyArgs))),
                DataType.Companion.ofNonNull(ClassDesc.of(id))));

        Expression equalsExpression;
        if (!dataVariables.isEmpty()) {
            equalsExpression = new OperatorExpression(
                    new VariableIdentifier(dataVariables.getFirst().getId()),
                    new MemberExpression(
                            new VariableIdentifier("value"),
                            new CallExpression(
                                    new FunctionIdentifier(MiscUtils.generatePrefixedName("get", dataVariables.getFirst().getId())),
                                    List.of()),
                            false),
                    "==", OperatorType.INFIX);
            for (int i = 1; i < dataVariables.size(); i++) {
                Parameter dataVariable = dataVariables.get(i);
                equalsExpression = new OperatorExpression(
                        equalsExpression,
                        new OperatorExpression(
                                new VariableIdentifier(dataVariable.getId()),
                                new MemberExpression(
                                        new VariableIdentifier("value"),
                                        new CallExpression(
                                                new FunctionIdentifier(MiscUtils.generatePrefixedName("get", dataVariable.getId())),
                                                List.of()),
                                        false),
                                "==", OperatorType.INFIX),
                        "&&", OperatorType.INFIX
                );
            }
        }
        else equalsExpression = new BooleanLiteral(true);
        body.add(new FunctionDeclarationStatement(
                Set.of(Modifiers.INSTANCE.getOperator()),
                "equals",
                List.of(new Parameter("value", DataType.Companion.ofNullable(ConstantDescs.CD_Object), true)),
                List.of(
                        new IfStatement(
                                new OperatorExpression(new VariableIdentifier("value"), new NullLiteral(), "==", OperatorType.INFIX),
                                List.of(new ReturnStatement(new BooleanLiteral(false))),
                                null),
                        new IfStatement(
                                new OperatorExpression(new IsExpression(new VariableIdentifier("value"), id, true), null, "!", OperatorType.PREFIX),
                                List.of(new ReturnStatement(new BooleanLiteral(false))),
                                null),
                        new ReturnStatement(equalsExpression)),
                DataType.Companion.ofNonNull(ConstantDescs.CD_boolean)
        ));

        return body;
    }

    private static Expression getToStringExpression(String id, List<Parameter> dataVariables) {
        Expression toStringExpression = new StringLiteral(id + "(" + (dataVariables.isEmpty() ? ")" : ""));
        for (int i = 0; i < dataVariables.size(); i++) {
            Parameter dataVariable = dataVariables.get(i);

            Expression endingExpression;
            if (i == dataVariables.size() - 1) endingExpression = new OperatorExpression(
                    new VariableIdentifier(dataVariable.getId()),
                    new StringLiteral(")"),
                    "+", OperatorType.INFIX
            );
            else endingExpression = new VariableIdentifier(dataVariable.getId());

            toStringExpression = new OperatorExpression(
                    toStringExpression,
                    new OperatorExpression(
                            new StringLiteral((i == 0 ? "" : ",") + dataVariable.getId() + "="),
                            endingExpression,
                            "+", OperatorType.INFIX),
                    "+", OperatorType.INFIX);
        }
        return toStringExpression;
    }

    private static FunctionDeclarationStatement getGetFunction(String id, DataType dataType) {
        return new FunctionDeclarationStatement(
                Set.of(),
                MiscUtils.generatePrefixedName("get", id),
                List.of(),
                List.of(new ReturnStatement(new VariableIdentifier(id))),
                dataType);
    }

    private static FunctionDeclarationStatement getSetFunction(String id, DataType dataType) {
        return new FunctionDeclarationStatement(
                Set.of(),
                MiscUtils.generatePrefixedName("set", id),
                List.of(new Parameter(id, dataType, true)),
                List.of(new AssignmentStatement(new MemberExpression(new ThisLiteral(), new VariableIdentifier(id), false), new VariableIdentifier(id))),
                null);
    }
}
