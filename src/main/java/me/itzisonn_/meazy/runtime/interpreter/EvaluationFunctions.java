package me.itzisonn_.meazy.runtime.interpreter;

import me.itzisonn_.meazy.parser.ast.Modifier;
import me.itzisonn_.meazy.parser.ast.Modifiers;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.expression.*;
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
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.environment.impl.default_classes.ListClassEnvironment;
import me.itzisonn_.meazy.runtime.values.*;
import me.itzisonn_.meazy.runtime.values.classes.ClassValue;
import me.itzisonn_.meazy.runtime.values.classes.RuntimeClassValue;
import me.itzisonn_.meazy.runtime.values.classes.DefaultClassValue;
import me.itzisonn_.meazy.runtime.values.classes.constructors.RuntimeConstructorValue;
import me.itzisonn_.meazy.runtime.values.classes.constructors.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.values.functions.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.functions.FunctionValue;
import me.itzisonn_.meazy.runtime.values.functions.RuntimeFunctionValue;
import me.itzisonn_.meazy.runtime.values.number.DoubleValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;
import me.itzisonn_.meazy.runtime.values.number.NumberValue;
import me.itzisonn_.meazy.runtime.values.statement_info.BreakInfoValue;
import me.itzisonn_.meazy.runtime.values.statement_info.ContinueInfoValue;
import me.itzisonn_.meazy.runtime.values.statement_info.ReturnInfoValue;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * All basic EvaluationFunctions
 *
 * @see Registries#EVALUATION_FUNCTIONS
 */
public final class EvaluationFunctions {
    private static boolean isInit = false;

    private EvaluationFunctions() {}



    /**
     * Initializes {@link Registries#EVALUATION_FUNCTIONS} registry
     * <p>
     * <i>Don't use this method because it's called once at {@link Registries} initialization</i>
     *
     * @throws IllegalStateException If {@link Registries#EVALUATION_FUNCTIONS} registry has already been initialized
     */
    public static void INIT() {
        if (isInit) throw new IllegalStateException("EvaluationFunctions have already been initialized!");
        isInit = true;

        register("program", Program.class, (program, environment, extra) -> {
            for (Statement statement : program.getBody()) {
                Interpreter.evaluate(statement, environment);
            }

            return null;
        });

        register("class_declaration_statement", ClassDeclarationStatement.class, (classDeclarationStatement, environment, extra) -> {
            if (!(environment instanceof ClassDeclarationEnvironment classDeclarationEnvironment)) {
                throw new InvalidSyntaxException("Can't declare class in this environment!");
            }

            for (Modifier modifier : classDeclarationStatement.getModifiers()) {
                if (!modifier.canUse(classDeclarationStatement, environment))
                    throw new InvalidSyntaxException("Can't use '" + modifier.getId() + "' AccessModifier");
            }

            ClassEnvironment classEnvironment;
            try {
                classEnvironment = Registries.CLASS_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class, boolean.class, String.class).newInstance(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), true, classDeclarationStatement.getId());
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            for (Statement statement : classDeclarationStatement.getBody()) {
                Interpreter.evaluate(statement, classEnvironment);
            }

            RuntimeClassValue runtimeClassValue = new RuntimeClassValue(classEnvironment, classDeclarationStatement.getBody());
            classDeclarationEnvironment.declareClass(runtimeClassValue);
            return null;
        });

        register("constructor_declaration_statement", ConstructorDeclarationStatement.class, (constructorDeclarationStatement, environment, extra) -> {
            if (!(environment instanceof ConstructorDeclarationEnvironment constructorDeclarationEnvironment)) {
                throw new InvalidSyntaxException("Can't declare constructor in this environment!");
            }

            for (Modifier modifier : constructorDeclarationStatement.getModifiers()) {
                if (!modifier.canUse(constructorDeclarationStatement, environment))
                    throw new InvalidSyntaxException("Can't use '" + modifier.getId() + "' AccessModifier");
            }

            RuntimeConstructorValue runtimeConstructorValue = new RuntimeConstructorValue(
                    constructorDeclarationStatement.getArgs(),
                    constructorDeclarationStatement.getBody(),
                    constructorDeclarationEnvironment,
                    constructorDeclarationStatement.getModifiers());

            constructorDeclarationEnvironment.declareConstructor(runtimeConstructorValue);
            return null;
        });

        register("function_declaration_statement", FunctionDeclarationStatement.class, (functionDeclarationStatement, environment, extra) -> {
            if (!(environment instanceof FunctionDeclarationEnvironment functionDeclarationEnvironment)) {
                throw new InvalidSyntaxException("Can't declare function in this environment!");
            }

            for (Modifier modifier : functionDeclarationStatement.getModifiers()) {
                if (!modifier.canUse(functionDeclarationStatement, environment))
                    throw new InvalidSyntaxException("Can't use '" + modifier.getId() + "' AccessModifier");
            }

            RuntimeFunctionValue runtimeFunctionValue = new RuntimeFunctionValue(
                    functionDeclarationStatement.getId(),
                    functionDeclarationStatement.getArgs(),
                    functionDeclarationStatement.getBody(),
                    functionDeclarationStatement.getReturnDataType(),
                    functionDeclarationEnvironment,
                    functionDeclarationStatement.getModifiers());

            functionDeclarationEnvironment.declareFunction(runtimeFunctionValue);
            return null;
        });

        register("variable_declaration_statement", VariableDeclarationStatement.class, (variableDeclarationStatement, environment, extra) -> {
            for (Modifier modifier : variableDeclarationStatement.getModifiers()) {
                if (!modifier.canUse(variableDeclarationStatement, environment)) throw new InvalidSyntaxException("Can't use '" + modifier.getId() + "' AccessModifier");
            }

            Set<Modifier> modifiers = new HashSet<>(variableDeclarationStatement.getModifiers());
            if (environment.isShared() && !variableDeclarationStatement.getModifiers().contains(Modifiers.SHARED())) modifiers.add(Modifiers.SHARED());

            variableDeclarationStatement.getDeclarationInfos().forEach(variableDeclarationInfo ->
                    environment.declareVariable(new VariableValue(
                            variableDeclarationInfo.getId(),
                            variableDeclarationInfo.getDataType(),
                            variableDeclarationInfo.getValue() == null ?
                                    null :
                                    Interpreter.evaluate(variableDeclarationInfo.getValue(), environment),
                            variableDeclarationStatement.isConstant(),
                            modifiers,
                            false)
            ));
            return null;
        });

        register("if_statement", IfStatement.class, (ifStatement, environment, extra) -> {
            while (ifStatement != null) {
                if (ifStatement.getCondition() != null) {
                    if (!parseCondition(ifStatement.getCondition(), environment)) {
                        ifStatement = ifStatement.getElseStatement();
                        continue;
                    }
                }

                Environment ifEnvironment;
                try {
                    ifEnvironment = Registries.ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class).newInstance(environment);
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }

                for (int i = 0; i < ifStatement.getBody().size(); i++) {
                    Statement statement = ifStatement.getBody().get(i);
                    RuntimeValue<?> result = Interpreter.evaluate(statement, ifEnvironment);

                    if (statement instanceof ReturnStatement) {
                        if (i + 1 < ifStatement.getBody().size()) throw new InvalidSyntaxException("Return statement must be last in body");
                        return new ReturnInfoValue(result);
                    }
                    if (result instanceof ReturnInfoValue returnInfoValue) {
                        return returnInfoValue;
                    }

                    if (statement instanceof ContinueStatement) {
                        if (i + 1 < ifStatement.getBody().size()) throw new InvalidSyntaxException("Continue statement must be last in body");
                        return new ContinueInfoValue();
                    }
                    if (result instanceof ContinueInfoValue continueInfoValue) {
                        return continueInfoValue;
                    }

                    if (statement instanceof BreakStatement) {
                        if (i + 1 < ifStatement.getBody().size()) throw new InvalidSyntaxException("Break statement must be last in body");
                        return new BreakInfoValue();
                    }
                    if (result instanceof BreakInfoValue breakInfoValue) {
                        return breakInfoValue;
                    }
                }
                break;
            }
            return null;
        });

        register("foreach_statement", ForeachStatement.class, (foreachStatement, environment, extra) -> {
            LoopEnvironment foreachEnvironment;
            try {
                foreachEnvironment = Registries.LOOP_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class).newInstance(environment);
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            RuntimeValue<?> rawCollectionValue = Interpreter.evaluate(foreachStatement.getCollection(), foreachEnvironment);
            if (!(rawCollectionValue instanceof ClassValue classValue && classValue.getId().equals("List")))
                throw new InvalidSyntaxException("Can't get members of non-list value");

            VariableValue variable = classValue.getEnvironment().getVariable("value");
            if (variable == null) throw new InvalidSyntaxException("Can't get members of non-list value");
            if (!(variable.getValue() instanceof ListClassEnvironment.InnerListValue listValue)) throw new InvalidSyntaxException("Can't get members of non-list value");

            main:
            for (RuntimeValue<?> runtimeValue : listValue.getValue()) {
                foreachEnvironment.clearVariables();

                foreachEnvironment.declareVariable(new VariableValue(
                        foreachStatement.getVariableDeclarationStatement().getDeclarationInfos().getFirst().getId(),
                        foreachStatement.getVariableDeclarationStatement().getDeclarationInfos().getFirst().getDataType(),
                        runtimeValue,
                        foreachStatement.getVariableDeclarationStatement().isConstant(),
                        new HashSet<>(),
                        false));

                for (int i = 0; i < foreachStatement.getBody().size(); i++) {
                    Statement statement = foreachStatement.getBody().get(i);
                    RuntimeValue<?> result = Interpreter.evaluate(statement, foreachEnvironment);

                    if (statement instanceof ReturnStatement) {
                        if (i + 1 < foreachStatement.getBody().size()) throw new InvalidSyntaxException("Return statement must be last in body");
                        return new ReturnInfoValue(result);
                    }
                    if (result instanceof ReturnInfoValue returnInfoValue) {
                        return returnInfoValue;
                    }

                    if (statement instanceof ContinueStatement) {
                        if (i + 1 < foreachStatement.getBody().size()) throw new InvalidSyntaxException("Continue statement must be last in body");
                        break;
                    }
                    if (result instanceof ContinueInfoValue) {
                        break;
                    }

                    if (statement instanceof BreakStatement) {
                        if (i + 1 < foreachStatement.getBody().size()) throw new InvalidSyntaxException("Break statement must be last in body");
                        break main;
                    }
                    if (result instanceof BreakInfoValue) {
                        break main;
                    }
                }
            }

            return null;
        });

        register("for_statement", ForStatement.class, (forStatement, environment, extra) -> {
            LoopEnvironment forEnvironment;
            try {
                forEnvironment = Registries.LOOP_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class).newInstance(environment);
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            forStatement.getVariableDeclarationStatement().getDeclarationInfos().forEach(variableDeclarationInfo ->
                    forEnvironment.declareVariable(new VariableValue(
                            variableDeclarationInfo.getId(),
                            variableDeclarationInfo.getDataType(),
                            variableDeclarationInfo.getValue() == null ?
                                    null :
                                    Interpreter.evaluate(variableDeclarationInfo.getValue(), environment),
                            forStatement.getVariableDeclarationStatement().isConstant(),
                            Set.of(),
                            false))
            );

            main:
            while (parseCondition(forStatement.getCondition(), forEnvironment)) {
                for (int i = 0; i < forStatement.getBody().size(); i++) {
                    Statement statement = forStatement.getBody().get(i);
                    RuntimeValue<?> result = Interpreter.evaluate(statement, forEnvironment);

                    if (statement instanceof ReturnStatement) {
                        if (i + 1 < forStatement.getBody().size()) throw new InvalidSyntaxException("Return statement must be last in body");
                        return new ReturnInfoValue(result);
                    }
                    if (result instanceof ReturnInfoValue returnInfoValue) {
                        return returnInfoValue;
                    }

                    if (statement instanceof ContinueStatement) {
                        if (i + 1 < forStatement.getBody().size()) throw new InvalidSyntaxException("Continue statement must be last in body");
                        break;
                    }
                    if (result instanceof ContinueInfoValue) {
                        break;
                    }

                    if (statement instanceof BreakStatement) {
                        if (i + 1 < forStatement.getBody().size()) throw new InvalidSyntaxException("Break statement must be last in body");
                        break main;
                    }
                    if (result instanceof BreakInfoValue) {
                        break main;
                    }
                }

                List<VariableValue> variableValues = new ArrayList<>();
                forStatement.getVariableDeclarationStatement().getDeclarationInfos().forEach(variableDeclarationInfo ->
                        variableValues.add(forEnvironment.getVariable(variableDeclarationInfo.getId())));

                forEnvironment.clearVariables();
                for (VariableValue variableValue : variableValues) {
                    forEnvironment.declareVariable(new VariableValue(
                            variableValue.getId(),
                            variableValue.getDataType(),
                            variableValue.getValue(),
                            variableValue.isConstant(),
                            new HashSet<>(),
                            false));
                }
                evaluateAssignmentExpression(forStatement.getAssignmentExpression(), forEnvironment);
            }

            return null;
        });

        register("while_statement", WhileStatement.class, (whileStatement, environment, extra) -> {
            LoopEnvironment whileEnvironment;
            try {
                whileEnvironment = Registries.LOOP_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class).newInstance(environment);
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            main:
            while (parseCondition(whileStatement.getCondition(), environment)) {
                whileEnvironment.clearVariables();

                for (int i = 0; i < whileStatement.getBody().size(); i++) {
                    Statement statement = whileStatement.getBody().get(i);
                    RuntimeValue<?> result = Interpreter.evaluate(statement, whileEnvironment);

                    if (statement instanceof ReturnStatement) {
                        if (i + 1 < whileStatement.getBody().size()) throw new InvalidSyntaxException("Return statement must be last in body");
                        return new ReturnInfoValue(result);
                    }
                    if (result instanceof ReturnInfoValue returnInfoValue) {
                        return returnInfoValue;
                    }

                    if (statement instanceof ContinueStatement) {
                        if (i + 1 < whileStatement.getBody().size()) throw new InvalidSyntaxException("Continue statement must be last in body");
                        break;
                    }
                    if (result instanceof ContinueInfoValue) {
                        break;
                    }

                    if (statement instanceof BreakStatement) {
                        if (i + 1 < whileStatement.getBody().size()) throw new InvalidSyntaxException("Break statement must be last in body");
                        break main;
                    }
                    if (result instanceof BreakInfoValue) {
                        break main;
                    }
                }
            }

            return null;
        });

        register("return_statement", ReturnStatement.class, (returnStatement, environment, extra) -> {
            if (environment instanceof FunctionEnvironment || environment.hasParent(parent -> parent instanceof FunctionEnvironment)) {
                if (returnStatement.getValue() == null) return null;
                return Interpreter.evaluate(returnStatement.getValue(), environment);
            }

            if (returnStatement.getValue() == null) {
                return null;
            }

            throw new InvalidSyntaxException("Can't return value not inside a function");
        });

        register("continue_statement", ContinueStatement.class, (continueStatement, environment, extra) -> {
            if (environment instanceof LoopEnvironment || environment.hasParent(parent -> parent instanceof LoopEnvironment)) {
                return null;
            }

            throw new InvalidSyntaxException("Can't use continue statement outside of for/while statements");
        });

        register("break_statement", BreakStatement.class, (breakStatement, environment, extra) -> {
            if (environment instanceof LoopEnvironment || environment.hasParent(parent -> parent instanceof LoopEnvironment)) {
                return null;
            }

            throw new InvalidSyntaxException("Can't use break statement outside of for/while statements");
        });

        register("assignment_expression", AssignmentExpression.class, (assignmentExpression, environment, extra) -> evaluateAssignmentExpression(assignmentExpression, environment));

        register("logical_expression", LogicalExpression.class, (logicalExpression, environment, extra) -> {
            RuntimeValue<?> left = Interpreter.evaluate(logicalExpression.getLeft(), environment).getFinalRuntimeValue();
            RuntimeValue<?> right = Interpreter.evaluate(logicalExpression.getRight(), environment).getFinalRuntimeValue();

            if (left instanceof BooleanValue leftValue && right instanceof BooleanValue rightValue) {
                boolean leftBoolean = leftValue.getValue();
                boolean rightBoolean = rightValue.getValue();

                return new BooleanValue(switch (logicalExpression.getOperator()) {
                    case "&&" -> leftBoolean && rightBoolean;
                    case "||" -> leftBoolean || rightBoolean;
                    default -> throw new UnsupportedOperatorException(logicalExpression.getOperator());
                });
            }

            throw new InvalidSyntaxException("Logical expression can't contain non-boolean values");
        });

        register("null_check_expression", NullCheckExpression.class, (nullCheckExpression, environment, extra) -> {
            RuntimeValue<?> checkValue = Interpreter.evaluate(nullCheckExpression.getCheckExpression(), environment).getFinalRuntimeValue();

            if (checkValue instanceof NullValue) {
                return Interpreter.evaluate(nullCheckExpression.getNullExpression(), environment).getFinalRuntimeValue();
            }
            return checkValue;
        });

        register("comparison_expression", ComparisonExpression.class, (comparisonExpression, environment, extra) -> {
            RuntimeValue<?> left = Interpreter.evaluate(comparisonExpression.getLeft(), environment).getFinalRuntimeValue();
            RuntimeValue<?> right = Interpreter.evaluate(comparisonExpression.getRight(), environment).getFinalRuntimeValue();

            if (left instanceof NumberValue<?> leftValue && right instanceof NumberValue<?> rightValue) {
                double leftNumber = leftValue.getValue().doubleValue();
                double rightNumber = rightValue.getValue().doubleValue();

                return new BooleanValue(switch (comparisonExpression.getOperator()) {
                    case "==" -> leftNumber == rightNumber;
                    case "!=" -> leftNumber != rightNumber;
                    case ">" -> leftNumber > rightNumber;
                    case ">=" -> leftNumber >= rightNumber;
                    case "<" -> leftNumber < rightNumber;
                    case "<=" -> leftNumber <= rightNumber;
                    default -> throw new UnsupportedOperatorException(comparisonExpression.getOperator());
                });
            }

            if (left instanceof StringValue leftValue && right instanceof StringValue rightValue) {
                String leftString = leftValue.getValue();
                String rightString = rightValue.getValue();

                return new BooleanValue(switch (comparisonExpression.getOperator()) {
                    case "==" -> leftString.equals(rightString);
                    case "!=" -> !leftString.equals(rightString);
                    case ">" -> leftString.length() > rightString.length();
                    case ">=" -> leftString.length() >= rightString.length();
                    case "<" -> leftString.length() < rightString.length();
                    case "<=" -> leftString.length() <= rightString.length();
                    default -> throw new UnsupportedOperatorException(comparisonExpression.getOperator());
                });
            }

            if (left instanceof BooleanValue leftValue && right instanceof BooleanValue rightValue) {
                boolean leftBoolean = leftValue.getValue();
                boolean rightBoolean = rightValue.getValue();

                return new BooleanValue(switch (comparisonExpression.getOperator()) {
                    case "==" -> leftBoolean == rightBoolean;
                    case "!=" -> leftBoolean != rightBoolean;
                    default -> throw new UnsupportedOperatorException(comparisonExpression.getOperator());
                });
            }

            if (left instanceof NullValue) {
                Object rightObject = right.getValue();

                return new BooleanValue(switch (comparisonExpression.getOperator()) {
                    case "==" -> rightObject == null;
                    case "!=" -> rightObject != null;
                    default -> throw new UnsupportedOperatorException(comparisonExpression.getOperator());
                });
            }

            if (right instanceof NullValue) {
                Object leftObject = left.getValue();

                return new BooleanValue(switch (comparisonExpression.getOperator()) {
                    case "==" -> leftObject == null;
                    case "!=" -> leftObject != null;
                    default -> throw new UnsupportedOperatorException(comparisonExpression.getOperator());
                });
            }

            throw new InvalidSyntaxException("Can't compare different values (" + left + " " + right + ")");
        });

        register("is_expression", IsExpression.class, (isExpression, environment, extra) -> {
            RuntimeValue<?> value = Interpreter.evaluate(isExpression.getValue(), environment).getFinalRuntimeValue();

            ClassValue classValue = Registries.GLOBAL_ENVIRONMENT.getEntry().getValue().getClass(isExpression.getDataType());
            if (classValue == null) throw new InvalidSyntaxException("Data type with id " + isExpression.getDataType() + " doesn't exist");

            return new BooleanValue(classValue.isMatches(value.getFinalRuntimeValue()));
        });

        register("binary_expression", BinaryExpression.class, (binaryExpression, environment, extra) -> {
            RuntimeValue<?> left = Interpreter.evaluate(binaryExpression.getLeft(), environment).getFinalRuntimeValue();
            RuntimeValue<?> right = Interpreter.evaluate(binaryExpression.getRight(), environment).getFinalRuntimeValue();

            if (left instanceof IntValue leftValue && right instanceof IntValue rightValue) {
                int leftNumber = leftValue.getValue();
                int rightNumber = rightValue.getValue();

                return new IntValue(switch (binaryExpression.getOperator()) {
                    case "+" -> leftNumber + rightNumber;
                    case "-" -> leftNumber - rightNumber;
                    case "*" -> leftNumber * rightNumber;
                    case "/" -> leftNumber / rightNumber;
                    case "%" -> leftNumber % rightNumber;
                    case "^" -> (int) Math.pow(leftNumber, rightNumber);
                    default -> throw new UnsupportedOperatorException(binaryExpression.getOperator());
                });
            }

            if (left instanceof NumberValue<?> leftValue && right instanceof NumberValue<?> rightValue) {
                double leftNumber = leftValue.getValue().doubleValue();
                double rightNumber = rightValue.getValue().doubleValue();

                return new DoubleValue(switch (binaryExpression.getOperator()) {
                    case "+" -> leftNumber + rightNumber;
                    case "-" -> leftNumber - rightNumber;
                    case "*" -> leftNumber * rightNumber;
                    case "/" -> leftNumber / rightNumber;
                    case "%" -> leftNumber % rightNumber;
                    case "^" -> Math.pow(leftNumber, rightNumber);
                    default -> throw new UnsupportedOperatorException(binaryExpression.getOperator());
                });
            }

            return new StringValue(switch (binaryExpression.getOperator()) {
                case "+" -> String.valueOf(left.getValue()) + right.getValue();
                case "*" -> {
                    String string;
                    int amount;

                    if (left instanceof StringValue stringValue && right instanceof IntValue numberValue) {
                        string = stringValue.getValue();
                        amount = numberValue.getValue();
                    }
                    else if (right instanceof StringValue stringValue && left instanceof IntValue numberValue) {
                        string = stringValue.getValue();
                        amount = numberValue.getValue();
                    }
                    else throw new InvalidSyntaxException("Can't multiply non-number values");

                    if (amount < 0) throw new InvalidSyntaxException("Can't multiply string by a negative int");

                    yield new StringBuilder().repeat(string, amount).toString();
                }
                default -> throw new UnsupportedOperatorException(binaryExpression.getOperator());
            });
        });

        register("inversion_expression", InversionExpression.class, (inversionExpression, environment, extra) -> {
            RuntimeValue<?> value = Interpreter.evaluate(inversionExpression.getExpression(), environment).getFinalRuntimeValue();
            if (!(value instanceof BooleanValue booleanValue)) throw new InvalidSyntaxException("Can't invert non-boolean value " + value);
            return new BooleanValue(!booleanValue.getValue());
        });

        register("function_call_expression", FunctionCallExpression.class, (functionCallExpression, environment, extra) -> {
            Environment extraEnvironment;
            if (extra.length == 0) extraEnvironment = environment;
            else if (extra[0] instanceof Environment extraEnv) extraEnvironment = extraEnv;
            else extraEnvironment = environment;

            List<RuntimeValue<?>> args = functionCallExpression.getArgs().stream().map(expression -> Interpreter.evaluate(expression, extraEnvironment)).collect(Collectors.toList());
            RuntimeValue<?> function = Interpreter.evaluate(functionCallExpression.getCaller(), environment, args);

            if (function instanceof DefaultFunctionValue defaultFunctionValue) {
                if (defaultFunctionValue.getArgs().size() != args.size()) {
                    throw new InvalidCallException("Expected " + defaultFunctionValue.getArgs().size() + " args but found " + args.size());
                }

                FunctionEnvironment functionEnvironment;
                try {
                    functionEnvironment = Registries.FUNCTION_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class, boolean.class)
                            .newInstance(defaultFunctionValue.getParentEnvironment(), defaultFunctionValue.getModifiers().contains(Modifiers.SHARED()));
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }

                RuntimeValue<?> returnValue = defaultFunctionValue.run(args, functionEnvironment);
                if (returnValue != null) returnValue = returnValue.getFinalRuntimeValue();
                return checkReturnValue(
                        returnValue,
                        defaultFunctionValue.getReturnDataType(),
                        defaultFunctionValue.getId(),
                        true);
            }
            if (function instanceof RuntimeFunctionValue runtimeFunctionValue) {
                if (runtimeFunctionValue.getArgs().size() != args.size()) {
                    throw new InvalidCallException("Expected " + runtimeFunctionValue.getArgs().size() + " args but found " + args.size());
                }

                FunctionEnvironment functionEnvironment;
                try {
                    functionEnvironment = Registries.FUNCTION_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class, boolean.class)
                            .newInstance(runtimeFunctionValue.getParentEnvironment(), runtimeFunctionValue.getModifiers().contains(Modifiers.SHARED()));
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }

                for (int i = 0; i < runtimeFunctionValue.getArgs().size(); i++) {
                    CallArgExpression callArgExpression = runtimeFunctionValue.getArgs().get(i);

                    functionEnvironment.declareVariable(new VariableValue(
                            callArgExpression.getId(),
                            callArgExpression.getDataType(),
                            args.get(i),
                            callArgExpression.isConstant(),
                            new HashSet<>(),
                            true));
                }

                RuntimeValue<?> result = null;
                boolean hasReturnStatement = false;
                for (int i = 0; i < runtimeFunctionValue.getBody().size(); i++) {
                    Statement statement = runtimeFunctionValue.getBody().get(i);
                    if (statement instanceof ReturnStatement) {
                        hasReturnStatement = true;
                        result = Interpreter.evaluate(statement, functionEnvironment);
                        if (result != null) {
                            checkReturnValue(
                                    result.getFinalRuntimeValue(),
                                    runtimeFunctionValue.getReturnDataType(),
                                    runtimeFunctionValue.getId(),
                                    false);
                        }
                        if (i + 1 < runtimeFunctionValue.getBody().size()) throw new InvalidSyntaxException("Return statement must be last in body");
                        break;
                    }
                    RuntimeValue<?> value = Interpreter.evaluate(statement, functionEnvironment);
                    if (value instanceof ReturnInfoValue returnInfoValue) {
                        hasReturnStatement = true;
                        result = returnInfoValue.getFinalRuntimeValue();
                        if (result.getFinalValue() != null) {
                            checkReturnValue(
                                    result.getFinalRuntimeValue(),
                                    runtimeFunctionValue.getReturnDataType(),
                                    runtimeFunctionValue.getId(),
                                    false);
                        }
                        break;
                    }
                }
                if ((result == null || result instanceof NullValue) && runtimeFunctionValue.getReturnDataType() != null) {
                    throw new InvalidSyntaxException(hasReturnStatement ?
                            "Function specified return value's data type but return statement is empty" : "Missing return statement");
                }
                return result;
            }

            throw new InvalidCallException("Can't call " + function.getValue() + " because it's not a function");
        });

        register("class_call_expression", ClassCallExpression.class, (classCallExpression, environment, extra) -> {
            Environment extraEnvironment;
            if (extra.length == 0) extraEnvironment = environment;
            else if (extra[0] instanceof Environment extraEnv) extraEnvironment = extraEnv;
            else extraEnvironment = environment;

            List<RuntimeValue<?>> args = classCallExpression.getArgs().stream().map(expression -> Interpreter.evaluate(expression, extraEnvironment)).collect(Collectors.toList());
            RuntimeValue<?> rawClass = Interpreter.evaluate(classCallExpression.getCaller(), environment);

            if (rawClass instanceof RuntimeClassValue runtimeClassValue) {
                ClassEnvironment classEnvironment;
                try {
                    classEnvironment = Registries.CLASS_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class, String.class).newInstance(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), runtimeClassValue.getId());
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }

                for (Statement statement : runtimeClassValue.getBody()) {
                    Interpreter.evaluate(statement, classEnvironment);
                }

                if (classEnvironment.hasConstructor()) {
                    RuntimeValue<?> rawConstructor = classEnvironment.getConstructor(args);
                    if (rawConstructor == null) throw new InvalidCallException("Class with id " + runtimeClassValue.getId() + " doesn't have requested constructor");

                    if (rawConstructor instanceof RuntimeConstructorValue runtimeConstructorValue) {
                        if (runtimeConstructorValue.getModifiers().contains(Modifiers.PRIVATE()) && !extraEnvironment.hasParent(env -> {
                            if (env instanceof ClassEnvironment classEnv) {
                                return classEnv.getId().equals(runtimeClassValue.getId());
                            }
                            return false;
                        })) {
                            throw new InvalidCallException("Requested constructor has private access");
                        }

                        Environment constructorEnvironment;
                        try {
                            constructorEnvironment = Registries.ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class).newInstance(classEnvironment);
                        }
                        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }

                        for (int i = 0; i < runtimeConstructorValue.getArgs().size(); i++) {
                            CallArgExpression callArgExpression = runtimeConstructorValue.getArgs().get(i);

                            constructorEnvironment.declareVariable(new VariableValue(
                                    callArgExpression.getId(),
                                    callArgExpression.getDataType(),
                                    args.get(i),
                                    callArgExpression.isConstant(),
                                    new HashSet<>(),
                                    true));
                        }

                        for (Statement statement : runtimeConstructorValue.getBody()) {
                            Interpreter.evaluate(statement, constructorEnvironment);
                        }
                    }
                }

                for (VariableValue variableValue : classEnvironment.getVariables()) {
                    if (variableValue.isConstant() && variableValue.getValue() == null) {
                        throw new InvalidSyntaxException("All empty constant variables must be initialized after constructor call");
                    }
                }

                return new RuntimeClassValue(classEnvironment, runtimeClassValue.getBody());
            }
            if (rawClass instanceof DefaultClassValue defaultClassValue) {
                ClassEnvironment classEnvironment;
                try {
                    classEnvironment = Registries.CLASS_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class, String.class).newInstance(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), defaultClassValue.getId());
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }

                defaultClassValue.getEnvironment().getVariables().forEach(variable -> classEnvironment.declareVariable(new VariableValue(
                        variable.getId(),
                        variable.getDataType(),
                        variable.getValue(),
                        variable.isConstant(),
                        variable.getModifiers(),
                        variable.isArgument())));
                defaultClassValue.getEnvironment().getFunctions().forEach(function -> {
                    if (function instanceof DefaultFunctionValue defaultFunctionValue) {
                        classEnvironment.declareFunction(defaultFunctionValue.copy(classEnvironment));
                    }
                });
                defaultClassValue.getEnvironment().getConstructors().forEach(constructor -> {
                    if (constructor instanceof DefaultConstructorValue defaultConstructorValue) {
                        classEnvironment.declareConstructor(defaultConstructorValue.copy(classEnvironment));
                    }
                });

                if (classEnvironment.hasConstructor()) {
                    RuntimeValue<?> rawConstructor = classEnvironment.getConstructor(args);
                    if (rawConstructor == null) throw new InvalidCallException("Class with id " + defaultClassValue.getId() + " doesn't have requested constructor");

                    if (rawConstructor instanceof DefaultConstructorValue defaultConstructorValue) {
                        if (defaultConstructorValue.getModifiers().contains(Modifiers.SHARED()) && !extraEnvironment.hasParent(env -> {
                            if (env instanceof ClassEnvironment classEnv) {
                                return classEnv.getId().equals(defaultClassValue.getId());
                            }
                            return false;
                        })) {
                            throw new InvalidCallException("Requested constructor has private access");
                        }

                        Environment constructorEnvironment;
                        try {
                            constructorEnvironment = Registries.ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class).newInstance(classEnvironment);
                        }
                        catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                               NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }

                        defaultConstructorValue.run(args, constructorEnvironment);
                    }
                }

                for (VariableValue variableValue : classEnvironment.getVariables()) {
                    if (variableValue.isConstant() && variableValue.getValue() == null) {
                        throw new InvalidSyntaxException("All empty constant variables must be initialized after constructor call. It's probably an Addon's error");
                    }
                }

                return new DefaultClassValue(classEnvironment);
            }

            throw new InvalidCallException("Can't call " + rawClass.getValue() + " because it's not a class");
        });

        register("member_expression", MemberExpression.class, (memberExpression, environment, extra) -> {
            RuntimeValue<?> value = Interpreter.evaluate(memberExpression.getObject(), environment).getFinalRuntimeValue();

            if (value instanceof NullValue) {
                if (memberExpression.isNullSafe()) return value;
                else throw new InvalidSyntaxException("Can't get member of null value");
            }

            if (value instanceof ClassValue classValue) {
                return Interpreter.evaluate(memberExpression.getMember(), classValue.getEnvironment(), environment);
            }

            throw new InvalidSyntaxException("Can't get member of " + value + " because it's not a class");
        });

        register("identifier", Identifier.class, new EvaluationFunction<>() {
            @Override
            public RuntimeValue<?> evaluate(Identifier identifier, Environment environment, Object... extra) {
                Environment requestEnvironment;
                if (extra.length == 0) requestEnvironment = environment;
                else if (extra[0] instanceof Environment env) requestEnvironment = env;
                else requestEnvironment = environment;

                if (identifier instanceof VariableIdentifier) {
                    VariableValue variableValue = environment.getVariableDeclarationEnvironment(identifier.getId()).getVariable(identifier.getId());
                    if (variableValue != null) {
                        if (variableValue.getModifiers().contains(Modifiers.PRIVATE()) && requestEnvironment != environment.getVariableDeclarationEnvironment(identifier.getId()) &&
                                !requestEnvironment.hasParent(environment.getVariableDeclarationEnvironment(identifier.getId())))
                            throw new InvalidAccessException("Can't access variable " + identifier.getId() + " because it's private");
                        if (!variableValue.getModifiers().contains(Modifiers.SHARED()) && environment.isShared() && !variableValue.isArgument())
                            throw new InvalidAccessException("Can't access variable " + identifier.getId() + " because it's not shared");

                        return variableValue;
                    }

                    throw new InvalidIdentifierException("Variable with identifier " + identifier.getId() + " doesn't exist");
                }

                if (identifier instanceof FunctionIdentifier) {
                    if (extra.length == 0 || !(extra[0] instanceof List<?> rawArgs)) throw new RuntimeException("Invalid function args");

                    List<RuntimeValue<?>> args = rawArgs.stream().map(object -> {
                        if (object instanceof RuntimeValue<?> arg) return arg;
                        throw new RuntimeException("Function args must be instance of RuntimeValue");
                    }).collect(Collectors.toList());

                    FunctionValue runtimeFunction = environment.getFunctionDeclarationEnvironment(identifier.getId(), args).getFunction(identifier.getId(), args);

                    if (runtimeFunction != null) {
                        if (runtimeFunction.getModifiers().contains(Modifiers.PRIVATE()) &&
                                !requestEnvironment.hasParent(environment.getFunctionDeclarationEnvironment(identifier.getId(), args)))
                            throw new InvalidAccessException("Can't access function " + identifier.getId() + " because it's private");
                        if (!runtimeFunction.getModifiers().contains(Modifiers.SHARED()) && environment.isShared())
                            throw new InvalidAccessException("Can't access function " + identifier.getId() + " because it's not shared");

                        return runtimeFunction;
                    }

                    throw new InvalidIdentifierException("Function with identifier " + identifier.getId() + " doesn't exist");
                }

                if (identifier instanceof ClassIdentifier) {
                    ClassValue classValue = Registries.GLOBAL_ENVIRONMENT.getEntry().getValue().getClass(identifier.getId());
                    if (classValue != null) return classValue;

                    return evaluate(new VariableIdentifier(identifier.getId()), environment, extra);
                }

                throw new InvalidIdentifierException("Invalid identifier " + identifier.getClass().getName());
            }
        });

        register("null_literal", NullLiteral.class, (nullLiteral, environment, extra) -> new NullValue());

        register("number_literal", NumberLiteral.class, (numberLiteral, environment, extra) -> {
            if (numberLiteral.isInt()) return new IntValue((int) numberLiteral.getValue());
            else return new DoubleValue(numberLiteral.getValue());
        });

        register("string_literal", StringLiteral.class, (stringLiteral, environment, extra) -> new StringValue(stringLiteral.getValue()));

        register("boolean_literal", BooleanLiteral.class, (booleanLiteral, environment, extra) -> new BooleanValue(booleanLiteral.isValue()));

        register("this_literal", ThisLiteral.class, (thisLiteral, environment, extra) -> {
            Environment parent = environment.getParent(env -> env instanceof ClassEnvironment);
            if (!(parent instanceof ClassEnvironment classEnvironment)) throw new RuntimeException("Can't use 'this' keyword not inside a class");
            if (environment.isShared()) throw new RuntimeException("Can't use 'this' keyword inside a shared environment");
            return new DefaultClassValue(classEnvironment);
        });
    }



    private static RuntimeValue<?> evaluateAssignmentExpression(AssignmentExpression assignmentExpression, Environment environment) {
        if (assignmentExpression.getId() instanceof VariableIdentifier variableIdentifier) {
            RuntimeValue<?> value = Interpreter.evaluate(assignmentExpression.getValue(), environment);
            environment.getVariableDeclarationEnvironment(variableIdentifier.getId()).assignVariable(variableIdentifier.getId(), value);
            return value;
        }
        if (assignmentExpression.getId() instanceof MemberExpression memberExpression) {
            RuntimeValue<?> memberExpressionValue = Interpreter.evaluate(memberExpression, environment);
            if (memberExpressionValue instanceof VariableValue variableValue) {
                RuntimeValue<?> value = Interpreter.evaluate(assignmentExpression.getValue(), environment);
                variableValue.setValue(value);
                return value;
            }
            throw new InvalidSyntaxException("Can't assign value to not variable " + memberExpressionValue);
        }
        throw new InvalidSyntaxException("Can't assign value to " + assignmentExpression.getId().getClass().getName());
    }

    private static boolean parseCondition(Expression rawCondition, Environment environment) {
        RuntimeValue<?> condition = Interpreter.evaluate(rawCondition, environment).getFinalRuntimeValue();

        if (!(condition instanceof BooleanValue booleanValue)) throw new InvalidArgumentException("Condition must be boolean value");
        return booleanValue.getValue();
    }

    private static RuntimeValue<?> checkReturnValue(RuntimeValue<?> returnValue, DataType returnDataType, String functionId, boolean isDefault) {
        String defaultString = isDefault ? ". It's probably an Addon's error" : "";

        if (returnValue == null) {
            if (returnDataType != null) {
                throw new InvalidSyntaxException("Didn't find return value but function with id " + functionId + " must return value" + defaultString);
            }
            return null;
        }
        if (returnDataType == null) {
            throw new InvalidSyntaxException("Found return value but function with id " + functionId + " must return nothing" + defaultString);
        }

        if (!returnDataType.isMatches(returnValue)) {
            throw new InvalidSyntaxException("Returned value's data type is different from specified (" + returnDataType.getId() + ")" + defaultString);
        }

        return returnValue;
    }



    private static <T extends Statement> void register(String id, Class<T> cls, EvaluationFunction<T> evaluationFunction) {
        Registries.EVALUATION_FUNCTIONS.register(RegistryIdentifier.ofDefault(id), cls, evaluationFunction);
    }
}