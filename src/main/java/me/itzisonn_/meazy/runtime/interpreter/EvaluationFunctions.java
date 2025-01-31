package me.itzisonn_.meazy.runtime.interpreter;

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
import me.itzisonn_.meazy.runtime.environment.RuntimeVariable;
import me.itzisonn_.meazy.runtime.environment.basic.default_classes.ListClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.environment.interfaces.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.LoopEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.VariableDeclarationEnvironment;
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
            if (environment instanceof ClassDeclarationEnvironment classDeclarationEnvironment) {
                ClassEnvironment classEnvironment;
                try {
                    classEnvironment = Registries.CLASS_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class, boolean.class, String.class).newInstance(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), true, classDeclarationStatement.getId());
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }

                for (Statement statement : classDeclarationStatement.getBody()) {
                    Interpreter.evaluate(statement, classEnvironment);
                }

                RuntimeClassValue runtimeClassValue = new RuntimeClassValue(classEnvironment, classDeclarationStatement.getBody());
                classDeclarationEnvironment.declareClass(classDeclarationStatement.getId(), runtimeClassValue);
                return null;
            }

            throw new InvalidSyntaxException("Can't declare class in this environment!");
        });

        register("constructor_declaration_statement", ConstructorDeclarationStatement.class, (constructorDeclarationStatement, environment, extra) -> {
            if (environment instanceof ConstructorDeclarationEnvironment constructorDeclarationEnvironment) {
                if (constructorDeclarationStatement.getAccessModifiers().contains("shared"))
                    throw new InvalidSyntaxException("Constructors can't have shared access modifier!");

                RuntimeConstructorValue runtimeConstructorValue = new RuntimeConstructorValue(
                        constructorDeclarationStatement.getArgs(),
                        constructorDeclarationStatement.getBody(),
                        constructorDeclarationEnvironment,
                        constructorDeclarationStatement.getAccessModifiers());

                constructorDeclarationEnvironment.declareConstructor(runtimeConstructorValue);
                return null;
            }

            throw new InvalidSyntaxException("Can't declare constructor in this environment!");
        });

        register("function_declaration_statement", FunctionDeclarationStatement.class, (functionDeclarationStatement, environment, extra) -> {
            if (environment instanceof FunctionDeclarationEnvironment functionDeclarationEnvironment) {
                RuntimeFunctionValue runtimeFunctionValue = new RuntimeFunctionValue(
                        functionDeclarationStatement.getId(),
                        functionDeclarationStatement.getArgs(),
                        functionDeclarationStatement.getBody(),
                        functionDeclarationStatement.getReturnDataType(),
                        functionDeclarationEnvironment,
                        functionDeclarationStatement.getAccessModifiers());

                functionDeclarationEnvironment.declareFunction(runtimeFunctionValue);
                return null;
            }

            throw new InvalidSyntaxException("Can't declare function in this environment!");
        });

        register("variable_declaration_statement", VariableDeclarationStatement.class, (variableDeclarationStatement, environment, extra) -> {
            if (!(environment instanceof VariableDeclarationEnvironment variableDeclarationEnvironment)) throw new InvalidSyntaxException("Can't declare variable in this environment!");

            Set<String> accessModifiers = new HashSet<>(variableDeclarationStatement.getAccessModifiers());
            if (!accessModifiers.contains("shared") && environment.isShared()) accessModifiers.add("shared");

            variableDeclarationStatement.getDeclarationInfos().forEach(variableDeclarationInfo ->
                    variableDeclarationEnvironment.declareVariable(
                            variableDeclarationInfo.getId(),
                            variableDeclarationInfo.getDataType(),
                            new VariableValue(variableDeclarationInfo.getValue() == null ?
                                    null :
                                    Interpreter.evaluate(variableDeclarationInfo.getValue(), environment), variableDeclarationEnvironment, variableDeclarationInfo.getId()),
                            variableDeclarationStatement.isConstant(),
                            accessModifiers)
            );
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
                    ifEnvironment = Registries.VARIABLE_DECLARATION_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class).newInstance(environment);
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
            List<RuntimeValue<?>> list;
            if (!(rawCollectionValue instanceof ClassValue classValue && classValue.getId().equals("List")))
                throw new InvalidSyntaxException("Can't get members of non-list value");
            RuntimeVariable variable = classValue.getClassEnvironment().getVariable("value");
            if (variable == null) throw new InvalidSyntaxException("Can't get members of non-list value");
            if (!(variable.getValue() instanceof ListClassEnvironment.InnerListValue listValue)) throw new InvalidSyntaxException("Can't get members of non-list value");
            list = listValue.getValue();

            main:
            for (RuntimeValue<?> runtimeValue : list) {
                foreachEnvironment.clearVariables();

                foreachEnvironment.declareVariable(
                        foreachStatement.getVariableDeclarationStatement().getDeclarationInfos().getFirst().getId(),
                        foreachStatement.getVariableDeclarationStatement().getDeclarationInfos().getFirst().getDataType(),
                        runtimeValue,
                        foreachStatement.getVariableDeclarationStatement().isConstant(),
                        new HashSet<>());

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
                    forEnvironment.declareVariable(
                            variableDeclarationInfo.getId(),
                            variableDeclarationInfo.getDataType(),
                            new VariableValue(variableDeclarationInfo.getValue() == null ?
                                    null :
                                    Interpreter.evaluate(variableDeclarationInfo.getValue(), environment), forEnvironment, variableDeclarationInfo.getId()),
                            forStatement.getVariableDeclarationStatement().isConstant(),
                            Set.of())
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

                List<RuntimeVariable> runtimeVariables = new ArrayList<>();
                forStatement.getVariableDeclarationStatement().getDeclarationInfos().forEach(variableDeclarationInfo ->
                        runtimeVariables.add(forEnvironment.getVariable(variableDeclarationInfo.getId())));

                forEnvironment.clearVariables();
                for (RuntimeVariable runtimeVariable : runtimeVariables) {
                    forEnvironment.declareVariable(
                            runtimeVariable.getId(),
                            runtimeVariable.getDataType(),
                            runtimeVariable.getValue(),
                            runtimeVariable.isConstant(),
                            new HashSet<>());
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
            if (returnStatement.getValue() == null &&
                    (environment instanceof VariableDeclarationEnvironment || environment.hasParent(parent -> parent instanceof VariableDeclarationEnvironment))) {
                return null;
            }

            throw new InvalidSyntaxException("Can only use return statement inside a function or if/for/while statements");
        });

        register("continue_statement", ContinueStatement.class, (continueStatement, environment, extra) -> {
            if (environment instanceof LoopEnvironment || environment.hasParent(parent -> parent instanceof LoopEnvironment)) {
                return null;
            }

            throw new InvalidSyntaxException("Can only use continue statement inside for/while statements");
        });

        register("break_statement", BreakStatement.class, (breakStatement, environment, extra) -> {
            if (environment instanceof LoopEnvironment || environment.hasParent(parent -> parent instanceof LoopEnvironment)) {
                return null;
            }

            throw new InvalidSyntaxException("Can only use break statement inside for/while statements");
        });

        register("assignment_expression", AssignmentExpression.class, (assignmentExpression, environment, extra) -> evaluateAssignmentExpression(assignmentExpression, environment));

        register("logical_expression", LogicalExpression.class, (logicalExpression, environment, extra) -> {
            RuntimeValue<?> left = Interpreter.evaluate(logicalExpression.getLeft(), environment).getFinalRuntimeValue();
            RuntimeValue<?> right = Interpreter.evaluate(logicalExpression.getRight(), environment).getFinalRuntimeValue();

            if (left instanceof BooleanValue leftValue && right instanceof BooleanValue rightValue) {
                boolean result;
                boolean leftBoolean = leftValue.getValue();
                boolean rightBoolean = rightValue.getValue();

                switch (logicalExpression.getOperator()) {
                    case "&&" -> result = leftBoolean && rightBoolean;
                    case "||" -> result = leftBoolean || rightBoolean;
                    default -> throw new UnsupportedOperatorException(logicalExpression.getOperator());
                }

                return new BooleanValue(result);
            }

            throw new InvalidSyntaxException("Logical expression must contain only boolean values");
        });

        register("comparison_expression", ComparisonExpression.class, (comparisonExpression, environment, extra) -> {
            RuntimeValue<?> left = Interpreter.evaluate(comparisonExpression.getLeft(), environment).getFinalRuntimeValue();
            RuntimeValue<?> right = Interpreter.evaluate(comparisonExpression.getRight(), environment).getFinalRuntimeValue();

            if (left instanceof NumberValue<?> leftValue && right instanceof NumberValue<?> rightValue) {
                boolean result;
                double leftNumber = leftValue.getValue().doubleValue();
                double rightNumber = rightValue.getValue().doubleValue();

                switch (comparisonExpression.getOperator()) {
                    case "==" -> result = leftNumber == rightNumber;
                    case "!=" -> result = leftNumber != rightNumber;
                    case ">" -> result = leftNumber > rightNumber;
                    case ">=" -> result = leftNumber >= rightNumber;
                    case "<" -> result = leftNumber < rightNumber;
                    case "<=" -> result = leftNumber <= rightNumber;
                    default -> throw new UnsupportedOperatorException(comparisonExpression.getOperator());
                }

                return new BooleanValue(result);
            }

            if (left instanceof StringValue leftValue && right instanceof StringValue rightValue) {
                boolean result;
                String leftNumber = leftValue.getValue();
                String rightNumber = rightValue.getValue();

                switch (comparisonExpression.getOperator()) {
                    case "==" -> result = leftNumber.equals(rightNumber);
                    case "!=" -> result = !leftNumber.equals(rightNumber);
                    case ">" -> result = leftNumber.length() > rightNumber.length();
                    case ">=" -> result = leftNumber.length() >= rightNumber.length();
                    case "<" -> result = leftNumber.length() < rightNumber.length();
                    case "<=" -> result = leftNumber.length() <= rightNumber.length();
                    default -> throw new UnsupportedOperatorException(comparisonExpression.getOperator());
                }

                return new BooleanValue(result);
            }

            if (left instanceof BooleanValue leftValue && right instanceof BooleanValue rightValue) {
                boolean result;
                boolean leftBoolean = leftValue.getValue();
                boolean rightBoolean = rightValue.getValue();

                switch (comparisonExpression.getOperator()) {
                    case "==" -> result = leftBoolean == rightBoolean;
                    case "!=" -> result = leftBoolean != rightBoolean;
                    default -> throw new UnsupportedOperatorException(comparisonExpression.getOperator());
                }

                return new BooleanValue(result);
            }

            if (left instanceof NullValue) {
                boolean result;
                Object rightObject = right.getValue();

                switch (comparisonExpression.getOperator()) {
                    case "==" -> result = null == rightObject;
                    case "!=" -> result = null != rightObject;
                    default -> throw new UnsupportedOperatorException(comparisonExpression.getOperator());
                }

                return new BooleanValue(result);
            }

            if (right instanceof NullValue) {
                boolean result;
                Object leftObject = left.getValue();

                switch (comparisonExpression.getOperator()) {
                    case "==" -> result = leftObject == null;
                    case "!=" -> result = leftObject != null;
                    default -> throw new UnsupportedOperatorException(comparisonExpression.getOperator());
                }

                return new BooleanValue(result);
            }

            throw new InvalidSyntaxException("Can't compare two different values" + left + " " + right);
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

            if (left instanceof NumberValue<?> leftValue && right instanceof NumberValue<?> rightValue) {
                return evaluateNumericBinaryExpression(leftValue, rightValue, binaryExpression.getOperator());
            }
            else {
                return evaluateStringBinaryExpression(left, right, binaryExpression.getOperator());
            }
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
                    functionEnvironment = Registries.FUNCTION_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class).newInstance(defaultFunctionValue.getParentEnvironment());
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
                    functionEnvironment = Registries.FUNCTION_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class).newInstance(runtimeFunctionValue.getParentEnvironment());
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }

                for (int i = 0; i < runtimeFunctionValue.getArgs().size(); i++) {
                    CallArgExpression callArgExpression = runtimeFunctionValue.getArgs().get(i);

                    functionEnvironment.declareArgument(
                            callArgExpression.getId(),
                            callArgExpression.getDataType(),
                            args.get(i),
                            callArgExpression.isConstant(),
                            new HashSet<>());
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
                        if (runtimeConstructorValue.getAccessModifiers().contains("private") && !extraEnvironment.hasParent(environment1 -> {
                            if (environment1 instanceof ClassEnvironment classEnvironment1) {
                                return classEnvironment1.getId().equals(runtimeClassValue.getId());
                            }
                            return false;
                        })) {
                            throw new InvalidCallException("Requested constructor has private access");
                        }

                        VariableDeclarationEnvironment constructorEnvironment;
                        try {
                            constructorEnvironment = Registries.VARIABLE_DECLARATION_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class).newInstance(classEnvironment);
                        }
                        catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }

                        for (int i = 0; i < runtimeConstructorValue.getArgs().size(); i++) {
                            CallArgExpression callArgExpression = runtimeConstructorValue.getArgs().get(i);

                            constructorEnvironment.declareArgument(
                                    callArgExpression.getId(),
                                    callArgExpression.getDataType(),
                                    args.get(i),
                                    callArgExpression.isConstant(),
                                    new HashSet<>());
                        }

                        for (Statement statement : runtimeConstructorValue.getBody()) {
                            Interpreter.evaluate(statement, constructorEnvironment);
                        }
                    }
                }

                for (RuntimeVariable runtimeVariable : classEnvironment.getVariables()) {
                    if (runtimeVariable.isConstant() && runtimeVariable.getValue().getFinalRuntimeValue() instanceof NullValue) {
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

                defaultClassValue.getClassEnvironment().getVariables().forEach(variable -> {
                    if (!variable.isArgument()) classEnvironment.declareVariable(variable.getId(), variable.getDataType(), variable.getValue(), variable.isConstant(), variable.getAccessModifiers());
                    else classEnvironment.declareArgument(variable.getId(), variable.getDataType(), variable.getValue(), variable.isConstant(), variable.getAccessModifiers());
                });
                defaultClassValue.getClassEnvironment().getFunctions().forEach(function -> {
                    if (function instanceof DefaultFunctionValue defaultFunctionValue) {
                        defaultFunctionValue.setParentEnvironment(classEnvironment);
                        classEnvironment.declareFunction(defaultFunctionValue);
                    }
                });
                defaultClassValue.getClassEnvironment().getConstructors().forEach(constructor -> {
                    if (constructor instanceof DefaultConstructorValue defaultConstructorValue) {
                        classEnvironment.declareConstructor(defaultConstructorValue.copy(classEnvironment));
                    }
                });

                if (classEnvironment.hasConstructor()) {
                    RuntimeValue<?> rawConstructor = classEnvironment.getConstructor(args);
                    if (rawConstructor == null) throw new InvalidCallException("Class with id " + defaultClassValue.getId() + " doesn't have requested constructor");

                    if (rawConstructor instanceof DefaultConstructorValue defaultConstructorValue) {
                        if (defaultConstructorValue.getAccessModifiers().contains("private") && !extraEnvironment.hasParent(environment1 -> {
                            if (environment1 instanceof ClassEnvironment classEnvironment1) {
                                return classEnvironment1.getId().equals(defaultClassValue.getId());
                            }
                            return false;
                        })) {
                            throw new InvalidCallException("Requested constructor has private access");
                        }

                        VariableDeclarationEnvironment constructorEnvironment;
                        try {
                            constructorEnvironment = Registries.VARIABLE_DECLARATION_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class).newInstance(classEnvironment);
                        }
                        catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                               NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        }

                        defaultConstructorValue.run(args, constructorEnvironment);
                    }
                }

                for (RuntimeVariable runtimeVariable : classEnvironment.getVariables()) {
                    if (runtimeVariable.isConstant() && runtimeVariable.getValue().getFinalRuntimeValue() instanceof NullValue) {
                        throw new InvalidSyntaxException("All empty constant variables must be initialized after constructor call. It's probably an Addon's error");
                    }
                }

                return new DefaultClassValue(classEnvironment);
            }

            throw new InvalidCallException("Can't call " + rawClass.getValue() + " because it's not a class");
        });

        register("member_expression", MemberExpression.class, (memberExpression, environment, extra) -> {
            RuntimeValue<?> value = Interpreter.evaluate(memberExpression.getObject(), environment).getFinalRuntimeValue();

            if (value instanceof ClassValue classValue) {
                return Interpreter.evaluate(memberExpression.getMember(), classValue.getClassEnvironment(), environment);
            }

            throw new InvalidSyntaxException("Can't get members of " + value + " because it's not a class");
        });

        register("identifier", Identifier.class, new EvaluationFunction<>() {
            @Override
            public RuntimeValue<?> evaluate(Identifier identifier, Environment environment, Object... extra) {
                if (identifier instanceof VariableIdentifier) {
                    RuntimeVariable runtimeVariable = environment.getVariableDeclarationEnvironment(identifier.getId()).getVariable(identifier.getId());
                    if (runtimeVariable != null) {
                        if (runtimeVariable.getAccessModifiers().contains("private") &&
                                !environment.hasParent(environment.getVariableDeclarationEnvironment(identifier.getId())))
                            throw new InvalidAccessException("Can't access variable " + identifier.getId() + " because it's private");
                        if (!runtimeVariable.getAccessModifiers().contains("shared") && environment.isShared() && !runtimeVariable.isArgument())
                            throw new InvalidAccessException("Can't access variable " + identifier.getId() + " because it's not shared");

                        return runtimeVariable.getValue();
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
                        if (runtimeFunction.getAccessModifiers().contains("private") &&
                                !environment.hasParent(environment.getFunctionDeclarationEnvironment(identifier.getId(), args)))
                            throw new InvalidAccessException("Can't access function " + identifier.getId() + " because it's private");
                        if (!runtimeFunction.getAccessModifiers().contains("shared") && environment.isShared())
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
            return new DefaultClassValue(classEnvironment);
        });
    }



    private static NumberValue<?> evaluateNumericBinaryExpression(NumberValue<?> leftValue, NumberValue<?> rightValue, String operator) {
        if (leftValue instanceof IntValue leftIntValue && rightValue instanceof IntValue rightIntValue) {
            int result;

            int left = leftIntValue.getValue();
            int right = rightIntValue.getValue();

            switch (operator) {
                case "+" -> result = left + right;
                case "-" -> result = left - right;
                case "*" -> result = left * right;
                case "/" -> result = left / right;
                case "%" -> result = left % right;
                case "^" -> result = (int) Math.pow(left, right);
                default -> throw new UnsupportedOperatorException(operator);
            }

            return new IntValue(result);
        }

        double result;

        double left = leftValue.getValue().doubleValue();
        double right = rightValue.getValue().doubleValue();

        switch (operator) {
            case "+" -> result = left + right;
            case "-" -> result = left - right;
            case "*" -> result = left * right;
            case "/" -> result = left / right;
            case "%" -> result = left % right;
            case "^" -> result = Math.pow(left, right);
            default -> throw new UnsupportedOperatorException(operator);
        }

        return new DoubleValue(result);
    }

    private static StringValue evaluateStringBinaryExpression(RuntimeValue<?> leftValue, RuntimeValue<?> rightValue, String operator) {
        StringBuilder result = new StringBuilder();

        switch (operator) {
            case "+" -> {
                String left = leftValue.getValue() == null ? "null" : leftValue.getValue().toString();
                String right = rightValue.getValue() == null ? "null" : rightValue.getValue().toString();
                result.append(left).append(right);
            }
            case "*" -> {
                String string;
                int number;

                if (leftValue instanceof StringValue stringValue && rightValue instanceof IntValue numberValue) {
                    string = stringValue.getValue();
                    number = numberValue.getValue();
                }
                else if (rightValue instanceof StringValue stringValue && leftValue instanceof IntValue numberValue) {
                    string = stringValue.getValue();
                    number = numberValue.getValue();
                }
                else throw new InvalidSyntaxException("Can only multiply string by a integer value");

                if (number < 0) throw new InvalidSyntaxException("Can't multiply string by a negative integer");

                result.repeat(string, number);
            }
            default -> throw new UnsupportedOperatorException(operator);
        }

        return new StringValue(result.toString());
    }

    private static RuntimeValue<?> evaluateAssignmentExpression(AssignmentExpression assignmentExpression, Environment environment) {
        if (assignmentExpression.getId() instanceof VariableIdentifier variableIdentifier) {
            if (!(environment instanceof VariableDeclarationEnvironment variableDeclarationEnvironment)) {
                throw new InvalidSyntaxException("Can't assign value not in variable declaration environment");
            }
            RuntimeValue<?> value = Interpreter.evaluate(assignmentExpression.getValue(), environment);
            if (!(value instanceof VariableValue)) value = new VariableValue(value,
                    variableDeclarationEnvironment.getVariableDeclarationEnvironment(variableIdentifier.getId()),
                    variableIdentifier.getId());
            variableDeclarationEnvironment.getVariableDeclarationEnvironment(variableIdentifier.getId()).assignVariable(variableIdentifier.getId(), value);
            return value;
        }
        if (assignmentExpression.getId() instanceof MemberExpression memberExpression) {
            RuntimeValue<?> memberExpressionValue = Interpreter.evaluate(memberExpression, environment);
            if (memberExpressionValue instanceof VariableValue variableValue) {
                RuntimeValue<?> value = Interpreter.evaluate(assignmentExpression.getValue(), environment);
                if (!(value instanceof VariableValue)) value = new VariableValue(value, variableValue.getParentEnvironment(), variableValue.getId());
                variableValue.getParentEnvironment().assignVariable(variableValue.getId(), value);
                return value;
            }
            throw new InvalidSyntaxException("Can't assign value to not variable");
        }
        throw new InvalidSyntaxException("Can't assign value to " + assignmentExpression.getId().getClass().getName());
    }

    private static boolean parseCondition(Expression rawCondition, Environment environment) {
        RuntimeValue<?> condition = Interpreter.evaluate(rawCondition, environment).getFinalRuntimeValue();

        if (!(condition instanceof BooleanValue booleanValue)) throw new InvalidArgumentException("Condition must be boolean value");
        return booleanValue.getValue();
    }

    private static RuntimeValue<?> checkReturnValue(RuntimeValue<?> returnValue, DataType returnDataType, String functionId, boolean isDefault) {
        String defaultString = isDefault ? " It's probably an Addon's error" : "";

        if (returnValue == null) {
            if (returnDataType != null) {
                throw new InvalidSyntaxException("Didn't find return value but function with id " + functionId + " must return value." + defaultString);
            }
            return null;
        }
        if (returnDataType == null) {
            throw new InvalidSyntaxException("Found return value but function with id " + functionId + " must return nothing." + defaultString);
        }

        if (!returnDataType.isMatches(returnValue)) {
            throw new InvalidSyntaxException("Returned value's data type is different from specified (" + returnDataType.getId() + ")." + defaultString);
        }

        return returnValue;
    }



    private static <T extends Statement> void register(String id, Class<T> cls, EvaluationFunction<T> evaluationFunction) {
        Registries.EVALUATION_FUNCTIONS.register(RegistryIdentifier.ofDefault(id), cls, evaluationFunction);
    }
}