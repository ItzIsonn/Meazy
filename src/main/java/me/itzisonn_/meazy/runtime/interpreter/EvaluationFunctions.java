package me.itzisonn_.meazy.runtime.interpreter;

import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.AccessModifiers;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.expression.*;
import me.itzisonn_.meazy.parser.ast.expression.call_expression.ClassCallExpression;
import me.itzisonn_.meazy.parser.ast.expression.call_expression.FunctionCallExpression;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.literal.BooleanLiteral;
import me.itzisonn_.meazy.parser.ast.expression.literal.NullLiteral;
import me.itzisonn_.meazy.parser.ast.expression.literal.NumberLiteral;
import me.itzisonn_.meazy.parser.ast.expression.literal.StringLiteral;
import me.itzisonn_.meazy.parser.ast.statement.*;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryIdentifier;
import me.itzisonn_.meazy.runtime.environment.RuntimeClass;
import me.itzisonn_.meazy.runtime.environment.RuntimeVariable;
import me.itzisonn_.meazy.runtime.environment.interfaces.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.environment.interfaces.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.LoopEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.VariableDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.values.*;
import me.itzisonn_.meazy.runtime.values.clazz.ClassValue;
import me.itzisonn_.meazy.runtime.values.clazz.DefaultClassValue;
import me.itzisonn_.meazy.runtime.values.clazz.constructor.ConstructorValue;
import me.itzisonn_.meazy.runtime.values.clazz.constructor.DefaultConstructorValue;
import me.itzisonn_.meazy.runtime.values.function.DefaultFunctionValue;
import me.itzisonn_.meazy.runtime.values.function.FunctionValue;
import me.itzisonn_.meazy.runtime.values.number.DoubleValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;
import me.itzisonn_.meazy.runtime.values.number.NumberValue;
import me.itzisonn_.meazy.runtime.values.statement_info.BreakInfoValue;
import me.itzisonn_.meazy.runtime.values.statement_info.ContinueInfoValue;
import me.itzisonn_.meazy.runtime.values.statement_info.ReturnInfoValue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class EvaluationFunctions {
    private static boolean isInit = false;

    private EvaluationFunctions() {}



    public static void INIT() {
        if (isInit) throw new IllegalStateException("EvaluationFunctions already initialized!");
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

                ClassValue classValue = new ClassValue(classEnvironment, classDeclarationStatement.getBody());
                classDeclarationEnvironment.declareClass(classDeclarationStatement.getId(), classValue);
                return null;
            }

            throw new InvalidSyntaxException("Can't declare class in this environment!");
        });

        register("constructor_declaration_statement", ConstructorDeclarationStatement.class, (constructorDeclarationStatement, environment, extra) -> {
            if (environment instanceof ConstructorDeclarationEnvironment constructorDeclarationEnvironment) {
                if (constructorDeclarationStatement.getAccessModifiers().contains(AccessModifiers.SHARED()))
                    throw new InvalidSyntaxException("Constructors can't have shared access modifier!");

                ConstructorValue constructorValue = new ConstructorValue(
                        constructorDeclarationStatement.getArgs(),
                        constructorDeclarationStatement.getBody(),
                        constructorDeclarationEnvironment,
                        constructorDeclarationStatement.getAccessModifiers());

                constructorDeclarationEnvironment.declareConstructor(constructorValue);
                return null;
            }

            throw new InvalidSyntaxException("Can't declare constructor in this environment!");
        });

        register("function_declaration_statement", FunctionDeclarationStatement.class, (functionDeclarationStatement, environment, extra) -> {
            if (environment instanceof FunctionDeclarationEnvironment functionDeclarationEnvironment) {
                RuntimeValue<?> arraySize = null;
                if (functionDeclarationStatement.getArraySize() != null) {
                    RuntimeValue<?> rawArraySize = Interpreter.evaluate(functionDeclarationStatement.getArraySize(), environment);
                    if (rawArraySize instanceof IntValue intValue) arraySize = intValue;
                    else if (rawArraySize instanceof NullValue nullValue) arraySize = nullValue;
                    else throw new InvalidSyntaxException("Array size must be int value");
                }

                FunctionValue functionValue = new FunctionValue(
                        functionDeclarationStatement.getId(),
                        functionDeclarationStatement.getArgs(),
                        functionDeclarationStatement.getBody(),
                        functionDeclarationStatement.getReturnDataType(),
                        arraySize,
                        functionDeclarationEnvironment,
                        functionDeclarationStatement.getAccessModifiers());

                functionDeclarationEnvironment.declareFunction(functionValue);
                return null;
            }

            throw new InvalidSyntaxException("Can't declare function in this environment!");
        });

        register("variable_declaration_statement", VariableDeclarationStatement.class, (variableDeclarationStatement, environment, extra) -> {
            if (!(environment instanceof VariableDeclarationEnvironment variableDeclarationEnvironment)) throw new InvalidSyntaxException("Can't declare variable in this environment!");

            Set<AccessModifier> accessModifiers = new HashSet<>(variableDeclarationStatement.getAccessModifiers());
            if (!accessModifiers.contains(AccessModifiers.SHARED()) && environment.isShared()) accessModifiers.add(AccessModifiers.SHARED());

            RuntimeValue<?> arraySize = null;
            if (variableDeclarationStatement.getArraySize() != null) {
                RuntimeValue<?> rawArraySize = Interpreter.evaluate(variableDeclarationStatement.getArraySize(), environment);
                if (rawArraySize instanceof IntValue intValue) arraySize = intValue;
                else if (rawArraySize instanceof NullValue nullValue) arraySize = nullValue;
                else throw new InvalidSyntaxException("Array size must be int value");
            }

            variableDeclarationEnvironment.declareVariable(
                    variableDeclarationStatement.getId(),
                    arraySize,
                    variableDeclarationStatement.getDataType(),
                    new VariableValue(variableDeclarationStatement.getValue() == null ? null : Interpreter.evaluate(variableDeclarationStatement.getValue(), environment), variableDeclarationEnvironment, variableDeclarationStatement.getId()),
                    variableDeclarationStatement.isConstant(),
                    accessModifiers);
            return null;
        });

        register("if_statement", IfStatement.class, (ifStatement, environment, extra) -> {
            while (ifStatement != null) {
                if (ifStatement.getCondition() != null) {
                    RuntimeValue<?> condition = Interpreter.evaluate(ifStatement.getCondition(), environment).getFinalRuntimeValue();
                    if (!(condition instanceof BooleanValue booleanValue)) throw new InvalidArgumentException("If condition must be boolean value");

                    if (!booleanValue.getValue()) {
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
            VariableDeclarationEnvironment foreachEnvironment;
            try {
                foreachEnvironment = Registries.LOOP_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class).newInstance(environment);
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            RuntimeValue<?> rawArrayValue = Interpreter.evaluate(foreachStatement.getCollection(), foreachEnvironment);
            ArrayList<RuntimeValue<?>> arrayList;
            if (rawArrayValue.getFinalRuntimeValue() instanceof ArrayValue arrayValue) arrayList = arrayValue.getValue();
            else throw new InvalidSyntaxException("Can't get members of non-array value");

            main:
            for (RuntimeValue<?> runtimeValue : arrayList) {
                foreachEnvironment.clearVariables();
                foreachEnvironment.declareVariable(
                        foreachStatement.getVariableDeclarationStatement().getId(),
                        null,
                        foreachStatement.getVariableDeclarationStatement().getDataType(),
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
            VariableDeclarationEnvironment forEnvironment;
            try {
                forEnvironment = Registries.LOOP_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class).newInstance(environment);
            }
            catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            IntValue arraySize = null;
            if (forStatement.getVariableDeclarationStatement().getArraySize() != null) {
                RuntimeValue<?> rawArraySize = Interpreter.evaluate(forStatement.getVariableDeclarationStatement().getArraySize(), environment);
                if (rawArraySize instanceof IntValue intValue) arraySize = intValue;
                else throw new InvalidSyntaxException("Array size must be int value");
            }

            forEnvironment.declareVariable(
                    forStatement.getVariableDeclarationStatement().getId(),
                    arraySize,
                    forStatement.getVariableDeclarationStatement().getDataType(),
                    Interpreter.evaluate(forStatement.getVariableDeclarationStatement().getValue(), forEnvironment),
                    forStatement.getVariableDeclarationStatement().isConstant(),
                    new HashSet<>());

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

                RuntimeVariable runtimeVariable = forEnvironment.getVariable(forStatement.getVariableDeclarationStatement().getId());
                forEnvironment.clearVariables();
                forEnvironment.declareVariable(
                        runtimeVariable.getId(),
                        runtimeVariable.getArraySize(),
                        runtimeVariable.getDataType(),
                        runtimeVariable.getValue(),
                        runtimeVariable.isConstant(),
                        new HashSet<>());
                forEnvironment.assignVariable(runtimeVariable.getId(), evaluateAssignmentExpression(forStatement.getAssignmentExpression(), forEnvironment));
            }

            return null;
        });

        register("while_statement", WhileStatement.class, (whileStatement, environment, extra) -> {
            VariableDeclarationEnvironment whileEnvironment;
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

        register("array_declaration_expression", ArrayDeclarationExpression.class, (arrayDeclarationExpression, environment, extra) -> {
            ArrayList<RuntimeValue<?>> args = new ArrayList<>();
            arrayDeclarationExpression.getValues().forEach(arg -> args.add(Interpreter.evaluate(arg, environment)));
            return new ArrayValue(args);
        });

        register("array_pointer_expression", ArrayPointerExpression.class, (arrayPointerExpression, environment, extra) -> {
            RuntimeValue<?> object = Interpreter.evaluate(arrayPointerExpression.getObject(), environment);
            if (object instanceof VariableValue variableValue) {
                if (!(variableValue.getParentEnvironment().getVariable(variableValue.getId()).getValue().getFinalValue() instanceof ArrayList<?> arrayList))
                    throw new InvalidSyntaxException("Can't get member of non-array");
                if (!(Interpreter.evaluate(arrayPointerExpression.getPos(), environment).getFinalRuntimeValue() instanceof IntValue pos))
                    throw new InvalidSyntaxException("Position of member in array must be int");
                if (pos.getValue() > arrayList.size())
                    throw new InvalidSyntaxException("Index " + pos.getValue() + " out of bounds for size " + arrayList.size());

                Object value = arrayList.get(pos.getValue());
                if (!(value instanceof RuntimeValue<?> runtimeValue)) return new NullValue();
                return runtimeValue;
            }
            throw new InvalidSyntaxException("Can't get member of non-variable value");
        });

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

            if (left instanceof ArrayValue leftValue && right instanceof ArrayValue rightValue) {
                boolean result;
                ArrayList<RuntimeValue<?>> leftArray = leftValue.getValue();
                ArrayList<RuntimeValue<?>> rightArray = rightValue.getValue();

                switch (comparisonExpression.getOperator()) {
                    case "==" -> result = leftArray.equals(rightArray);
                    case "!=" -> result = !leftArray.equals(rightArray);
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

        register("function_call_expression", FunctionCallExpression.class, (functionCallExpression, environment, extra) -> {
            Environment extraEnvironment = extra.length == 0 ? environment : extra[0];

            ArrayList<RuntimeValue<?>> args = new ArrayList<>();
            functionCallExpression.getArgs().forEach(arg -> args.add(Interpreter.evaluate(arg, extraEnvironment)));

            RuntimeValue<?> function = Interpreter.evaluate(functionCallExpression.getCaller(), environment, extraEnvironment);

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
                        defaultFunctionValue.getArraySize(),
                        defaultFunctionValue.getId(),
                        true);
            }
            if (function instanceof FunctionValue functionValue) {
                if (functionValue.getArgs().size() != args.size()) {
                    throw new InvalidCallException("Expected " + functionValue.getArgs().size() + " args but found " + args.size());
                }

                FunctionEnvironment functionEnvironment;
                try {
                    functionEnvironment = Registries.FUNCTION_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class).newInstance(functionValue.getParentEnvironment());
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }

                for (int i = 0; i < functionValue.getArgs().size(); i++) {
                    CallArgExpression callArgExpression = functionValue.getArgs().get(i);

                    RuntimeValue<?> arraySize = null;
                    if (callArgExpression.getArraySize() != null) {
                        RuntimeValue<?> rawArraySize = Interpreter.evaluate(callArgExpression.getArraySize(), environment);
                        if (rawArraySize instanceof IntValue intValue) arraySize = intValue;
                        else if (rawArraySize instanceof NullValue nullValue) arraySize = nullValue;
                        else throw new InvalidSyntaxException("Array size must be int value");
                    }

                    functionEnvironment.declareArgument(
                            callArgExpression.getId(),
                            arraySize,
                            callArgExpression.getDataType(),
                            args.get(i),
                            callArgExpression.isConstant(),
                            new HashSet<>());
                }

                RuntimeValue<?> result = null;
                boolean hasReturnStatement = false;
                for (int i = 0; i < functionValue.getBody().size(); i++) {
                    Statement statement = functionValue.getBody().get(i);
                    if (statement instanceof ReturnStatement) {
                        hasReturnStatement = true;
                        result = Interpreter.evaluate(statement, functionEnvironment);
                        if (result != null) {
                            checkReturnValue(
                                    result.getFinalRuntimeValue(),
                                    functionValue.getReturnDataType(),
                                    functionValue.getArraySize(),
                                    functionValue.getId(),
                                    false);
                        }
                        if (i + 1 < functionValue.getBody().size()) throw new InvalidSyntaxException("Return statement must be last in body");
                        break;
                    }
                    RuntimeValue<?> value = Interpreter.evaluate(statement, functionEnvironment);
                    if (value instanceof ReturnInfoValue returnInfoValue) {
                        hasReturnStatement = true;
                        result = returnInfoValue.getFinalRuntimeValue();
                        if (result.getFinalValue() != null) {
                            checkReturnValue(
                                    result.getFinalRuntimeValue(),
                                    functionValue.getReturnDataType(),
                                    functionValue.getArraySize(),
                                    functionValue.getId(),
                                    false);
                        }
                        break;
                    }
                }
                if ((result == null || result instanceof NullValue) && functionValue.getReturnDataType() != null) {
                    throw new InvalidSyntaxException(hasReturnStatement ?
                            "Function specified return value's data type but return statement is empty" : "Missing return statement");
                }
                return result;
            }

            throw new InvalidCallException("Can't call " + function.getValue() + " because it's not a function");
        });

        register("class_call_expression", ClassCallExpression.class, (classCallExpression, environment, extra) -> {
            Environment extraEnvironment = extra.length == 0 ? environment : extra[0];

            ArrayList<RuntimeValue<?>> args = new ArrayList<>();
            classCallExpression.getArgs().forEach(arg -> args.add(Interpreter.evaluate(arg, extraEnvironment)));

            RuntimeValue<?> rawClass = Interpreter.evaluate(classCallExpression.getCaller(), environment);

            if (rawClass instanceof ClassValue classValue) {
                ClassEnvironment classEnvironment;
                try {
                    classEnvironment = Registries.CLASS_ENVIRONMENT.getEntry().getValue().getConstructor(Environment.class, String.class).newInstance(Registries.GLOBAL_ENVIRONMENT.getEntry().getValue(), classValue.getId());
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }

                for (Statement statement : classValue.getBody()) {
                    Interpreter.evaluate(statement, classEnvironment);
                }

                if (classEnvironment.hasConstructor()) {
                    RuntimeValue<?> rawConstructor = classEnvironment.getConstructor(args);
                    if (rawConstructor == null) throw new InvalidCallException("Class with id " + classValue.getId() + " doesn't have requested constructor");

                    if (rawConstructor instanceof ConstructorValue constructorValue) {
                        if (constructorValue.getAccessModifiers().contains(AccessModifiers.PRIVATE()) && !extraEnvironment.hasParent(environment1 -> {
                            if (environment1 instanceof ClassEnvironment classEnvironment1) {
                                return classEnvironment1.getId().equals(classValue.getId());
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

                        for (int i = 0; i < constructorValue.getArgs().size(); i++) {
                            CallArgExpression callArgExpression = constructorValue.getArgs().get(i);

                            RuntimeValue<?> arraySize = null;
                            if (callArgExpression.getArraySize() != null) {
                                RuntimeValue<?> rawArraySize = Interpreter.evaluate(callArgExpression.getArraySize(), environment);
                                if (rawArraySize instanceof IntValue intValue) arraySize = intValue;
                                else if (rawArraySize instanceof NullValue nullValue) arraySize = nullValue;
                                else throw new InvalidSyntaxException("Array size must be int value");
                            }

                            constructorEnvironment.declareArgument(
                                    callArgExpression.getId(),
                                    arraySize,
                                    callArgExpression.getDataType(),
                                    args.get(i),
                                    callArgExpression.isConstant(),
                                    new HashSet<>());
                        }

                        for (Statement statement : constructorValue.getBody()) {
                            Interpreter.evaluate(statement, constructorEnvironment);
                        }
                    }
                }

                for (RuntimeVariable runtimeVariable : classEnvironment.getVariables()) {
                    if (runtimeVariable.isConstant() && runtimeVariable.getValue().getFinalRuntimeValue() instanceof NullValue) {
                        throw new InvalidSyntaxException("All empty constant variables must be initialized after constructor call");
                    }
                }

                return new ClassValue(classEnvironment, classValue.getBody());
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
                    if (!variable.isArgument()) classEnvironment.declareVariable(variable.getId(), variable.getArraySize(), variable.getDataType(), variable.getValue(), variable.isConstant(), variable.getAccessModifiers());
                    else classEnvironment.declareArgument(variable.getId(), variable.getArraySize(), variable.getDataType(), variable.getValue(), variable.isConstant(), variable.getAccessModifiers());
                });
                defaultClassValue.getClassEnvironment().getFunctions().forEach(function -> {
                    if (function.getFunctionValue() instanceof DefaultFunctionValue defaultFunctionValue) {
                        defaultFunctionValue.setParentEnvironment(classEnvironment);
                        classEnvironment.declareFunction(defaultFunctionValue);
                    }
                });
                defaultClassValue.getClassEnvironment().getConstructors().forEach(constructor -> {
                    if (constructor instanceof DefaultConstructorValue defaultConstructorValue) {
                        defaultConstructorValue.setParentEnvironment(classEnvironment);
                        classEnvironment.declareConstructor(defaultConstructorValue);
                    }
                });

                if (classEnvironment.hasConstructor()) {
                    RuntimeValue<?> rawConstructor = classEnvironment.getConstructor(args);
                    if (rawConstructor == null) throw new InvalidCallException("Class with id " + defaultClassValue.getId() + " doesn't have requested constructor");

                    if (rawConstructor instanceof DefaultConstructorValue defaultConstructorValue) {
                        if (defaultConstructorValue.getAccessModifiers().contains(AccessModifiers.PRIVATE()) && !extraEnvironment.hasParent(environment1 -> {
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
            RuntimeValue<?> object = Interpreter.evaluate(memberExpression.getObject(), environment);

            if (object instanceof VariableValue variableValue) {
                if (variableValue.getValue() instanceof ClassValue classValue) {
                    return Interpreter.evaluate(memberExpression.getField(), classValue.getClassEnvironment(), environment);
                }
                if (variableValue.getValue() instanceof DefaultClassValue defaultClassValue) {
                    return Interpreter.evaluate(memberExpression.getField(), defaultClassValue.getClassEnvironment(), environment);
                }
                throw new InvalidSyntaxException("Can't get members of " + object.getValue() + " because it's not a class");
            }
            if (object instanceof ClassValue classValue) {
                return Interpreter.evaluate(memberExpression.getField(), classValue.getClassEnvironment(), environment);
            }
            if (object instanceof DefaultClassValue defaultClassValue) {
                return Interpreter.evaluate(memberExpression.getField(), defaultClassValue.getClassEnvironment(), environment);
            }

            throw new InvalidSyntaxException("Can't get members of " + object.getValue() + " because it's not a class");
        });

        register("identifier", Identifier.class, new EvaluationFunction<>() {
            @Override
            public RuntimeValue<?> evaluateStatement(Identifier identifier, Environment environment, Environment... extra) {
                if (identifier instanceof VariableIdentifier) {
                    RuntimeVariable runtimeVariable = environment.getVariableEnvironment(identifier.getId()).getVariable(identifier.getId());
                    if (runtimeVariable != null) {
                        if (runtimeVariable.getAccessModifiers().contains(AccessModifiers.PRIVATE()) &&
                                !environment.hasParent(environment.getVariableEnvironment(identifier.getId())))
                            throw new InvalidAccessException("Can't access variable " + identifier.getId() + " because it's private");
                        if (!runtimeVariable.getAccessModifiers().contains(AccessModifiers.SHARED()) && environment.isShared() && !runtimeVariable.isArgument())
                            throw new InvalidAccessException("Can't access variable " + identifier.getId() + " because it's not shared");

                        return runtimeVariable.getValue();
                    }

                    throw new InvalidIdentifierException("Variable with identifier " + identifier.getId() + " doesn't exist");
                }

                if (identifier instanceof FunctionIdentifier functionIdentifier) {
                    ArrayList<RuntimeValue<?>> args = new ArrayList<>();
                    functionIdentifier.getArgs().forEach(arg -> args.add(Interpreter.evaluate(arg, extra[0])));
                    RuntimeValue<?> runtimeFunction = environment.getFunctionEnvironment(identifier.getId(), args).getFunction(identifier.getId(), args);

                    if (runtimeFunction != null) {
                        if (runtimeFunction instanceof FunctionValue functionValue) {
                            if (functionValue.getAccessModifiers().contains(AccessModifiers.PRIVATE()) &&
                                    !environment.hasParent(environment.getFunctionEnvironment(identifier.getId(), args)))
                                throw new InvalidAccessException("Can't access function " + identifier.getId() + " because it's private");
                            if (!functionValue.getAccessModifiers().contains(AccessModifiers.SHARED()) && environment.isShared())
                                throw new InvalidAccessException("Can't access function " + identifier.getId() + " because it's not shared");
                        } else if (runtimeFunction instanceof DefaultFunctionValue defaultFunctionValue) {
                            if (defaultFunctionValue.getAccessModifiers().contains(AccessModifiers.PRIVATE()) &&
                                    !environment.hasParent(environment.getFunctionEnvironment(identifier.getId(), args)))
                                throw new InvalidAccessException("Can't access function " + identifier.getId() + " because it's private");
                            if (!defaultFunctionValue.getAccessModifiers().contains(AccessModifiers.SHARED()) && environment.isShared())
                                throw new InvalidAccessException("Can't access function " + identifier.getId() + " because it's not shared");
                        }

                        return runtimeFunction;
                    }

                    throw new InvalidIdentifierException("Function with identifier " + identifier.getId() + " doesn't exist");
                }

                if (identifier instanceof ClassIdentifier) {
                    RuntimeClass runtimeClass = Registries.GLOBAL_ENVIRONMENT.getEntry().getValue().getClass(identifier.getId());
                    if (runtimeClass != null) return runtimeClass.getRuntimeValue();

                    return evaluateStatement(new VariableIdentifier(identifier.getId()), environment, extra);
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

    @SuppressWarnings("unchecked")
    private static RuntimeValue<?> evaluateAssignmentExpression(AssignmentExpression assignmentExpression, Environment environment) {
        if (assignmentExpression.getId() instanceof VariableIdentifier variableIdentifier) {
            if (!(environment instanceof VariableDeclarationEnvironment variableDeclarationEnvironment)) {
                throw new InvalidSyntaxException("Can't assign value not in variable declaration environment");
            }
            RuntimeValue<?> value = Interpreter.evaluate(assignmentExpression.getValue(), environment);
            if (!(value instanceof VariableValue)) value = new VariableValue(value,
                    variableDeclarationEnvironment.getVariableEnvironment(variableIdentifier.getId()),
                    variableIdentifier.getId());
            variableDeclarationEnvironment.getVariableEnvironment(variableIdentifier.getId()).assignVariable(variableIdentifier.getId(), value);
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
        if (assignmentExpression.getId() instanceof ArrayPointerExpression arrayPointerExpression) {
            RuntimeValue<?> object = Interpreter.evaluate(arrayPointerExpression.getObject(), environment);
            if (object instanceof VariableValue variableValue) {
                RuntimeVariable variable = variableValue.getParentEnvironment().getVariable(variableValue.getId());
                if (!(variable.getValue().getFinalValue() instanceof ArrayList<?> arrayList))
                    throw new InvalidSyntaxException("Can't assign value to member of non-array");
                if (!(Interpreter.evaluate(arrayPointerExpression.getPos(), environment).getFinalRuntimeValue() instanceof IntValue pos))
                    throw new InvalidSyntaxException("Position of member in array must be int");
                if (pos.getValue() >= arrayList.size())
                    throw new InvalidSyntaxException("Index " + pos.getValue() + " out of bounds for size " + arrayList.size());

                RuntimeValue<?> value = Interpreter.evaluate(assignmentExpression.getValue(), environment);
                if (!variable.getDataType().isMatches(value.getFinalRuntimeValue()))
                    throw new InvalidSyntaxException("Variable with id " + variable.getId() + " requires data type " + variable.getDataType().getName());
                ((ArrayList<Object>)arrayList).set(pos.getValue(), value);
                return value;
            }
            throw new InvalidSyntaxException("Can't assign value to member of non-variable value" + object);
        }
        throw new InvalidSyntaxException("Can't assign value to " + assignmentExpression.getId().getClass().getName());
    }

    private static boolean parseCondition(Expression rawCondition, Environment environment) {
        RuntimeValue<?> condition = Interpreter.evaluate(rawCondition, environment).getFinalRuntimeValue();

        if (!(condition instanceof BooleanValue booleanValue)) throw new InvalidArgumentException("Condition must be boolean value");
        return booleanValue.getValue();
    }

    private static RuntimeValue<?> checkReturnValue(RuntimeValue<?> returnValue, DataType returnDataType, IntValue arraySizeValue, String functionId, boolean isDefault) {
        String defaultString = isDefault ? " It's probably an Addon's error" : "";

        if (returnValue != null && returnDataType == null)
            throw new InvalidSyntaxException("Found return value but function with id " + functionId + " must return nothing." + defaultString);
        if (returnValue == null) {
            if (returnDataType != null)
                throw new InvalidSyntaxException("Didn't find return value but function with id " + functionId + " must return value." + defaultString);
            return null;
        }

        if (arraySizeValue != null) {
            if (!(returnValue.getFinalRuntimeValue() instanceof ArrayValue arrayValue))
                throw new InvalidSyntaxException("Function with id " + functionId + " requires array value." + defaultString);

            int arraySize = arraySizeValue.getValue();
            if (arraySize == -1) arraySize = arrayValue.getValue().size();
            if (arraySize != arrayValue.getValue().size())
                throw new InvalidSyntaxException("Array size of function with id " + functionId + " doesn't match with assigned value." + defaultString);

            for (RuntimeValue<?> runtimeValue : arrayValue.getValue()) {
                if (!returnDataType.isMatches(runtimeValue.getFinalRuntimeValue()))
                    throw new InvalidSyntaxException("Function with id " + functionId + " requires data type " + returnDataType.getName() + "." + defaultString);
            }
            return returnValue;
        }
        else if (returnValue instanceof ArrayValue) throw new InvalidSyntaxException("Function with id " + functionId + " can't return array value." + defaultString);

        if (!returnDataType.isMatches(returnValue.getFinalRuntimeValue()))
            throw new InvalidSyntaxException("Returned value's data type is different from specified." + defaultString);

        return returnValue;
    }



    private static <T extends Statement> void register(String id, Class<T> cls, EvaluationFunction<T> evaluationFunction) {
        Registries.EVALUATION_FUNCTION.register(RegistryIdentifier.ofDefault(id), cls, evaluationFunction);
    }
}