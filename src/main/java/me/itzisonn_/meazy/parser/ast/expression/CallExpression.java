package me.itzisonn_.meazy.parser.ast.expression;

import kotlin.Unit;
import kotlin.uuid.Uuid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.literal.ThisLiteral;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.util.MiscUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.util.List;

@Getter
@NullMarked
public class CallExpression implements Expression, LocalStatement {
    private final Identifier caller;
    private final List<Expression> args;

    public CallExpression(Identifier caller, List<Expression> args) {
        this.caller = caller;
        this.args = args;
    }

    @Override
    public void emit(InstructionsSet instructions, Environment environment, ProgramUnit parent) {
        if (caller instanceof FunctionIdentifier) {
            ResolvedCallable resolvedFunction = resolveFunction(environment, parent);
            Uuid endLabel = null;

            if (resolvedFunction.getTarget() != null) {
                resolvedFunction.getTarget().emit(instructions, environment, this);

                if (parent instanceof MemberExpression memberExpression) {
                    if (!memberExpression.isNullSafe()) {
                        if (resolvedFunction.getTarget().getType(environment, this).isNullable()) {
                            throw new RuntimeException("Unsafe member call of function " + caller.getId() + " on object of type " + resolvedFunction.getClassDesc().descriptorString());
                        }
                    }
                    else {
                        var nonnullLabel = instructions.createAndInitLabel();
                        endLabel = instructions.createAndInitLabel();

                        instructions.duplicate();
                        instructions.gotoLabelIfNonNull(nonnullLabel);

                        instructions.pop();
                        instructions.loadNull();
                        instructions.gotoLabel(endLabel);

                        instructions.bindLabel(nonnullLabel);
                    }
                }
            }

            instructions.invokeMethod(
                    resolvedFunction.getClassDesc(),
                    caller.getId(),
                    resolvedFunction.getMethodTypeDesc(),
                    argsInstructions -> {
                        for (int i = 0; i < args.size(); i++) {
                            ClassDesc parameterType = resolvedFunction.getMethodTypeDesc().parameterType(i);

                            Expression arg = args.get(i);
                            ClassDesc argType = arg.getType(environment, this).getClassDesc();

                            arg.emit(argsInstructions, environment, this);

                            if (!EnvironmentUtils.isInstanceOf(environment, argType, parameterType)) {
                                if (!MiscUtils.convertPrimitiveOrBoxed(instructions, argType, parameterType)) {
                                    throw new RuntimeException("Can't pass argument of type " + argType + " to parameter of type " + parameterType);
                                }
                            }
                        }

                        return Unit.INSTANCE;
                    },
                    resolvedFunction.getTarget() == null ?
                            resolvedFunction.isInterface() ? InvokeType.STATIC_INTERFACE : InvokeType.STATIC :
                            resolvedFunction.isInterface() ? InvokeType.INTERFACE : InvokeType.VIRTUAL
            );

            if (endLabel != null) {
                instructions.bindLabel(endLabel);
            }
        }

        else if (caller instanceof ClassIdentifier) {
            ResolvedCallable resolvedConstructor = resolveConstructor(environment);

            instructions.invokeConstructor(
                    resolvedConstructor.getClassDesc(),
                    resolvedConstructor.getMethodTypeDesc(),
                    argsInstructions -> {
                        for (Expression arg : args) {
                            arg.emit(argsInstructions, environment, this);
                        }

                        return Unit.INSTANCE;
                    }
            );
        }

        else throw new RuntimeException("Unknown caller TODO " + caller.getClass().getName());
    }

    @Override
    public DataType getType(Environment environment, ProgramUnit parent) {
        if (caller instanceof FunctionIdentifier) {
            ResolvedCallable function = resolveFunction(environment, parent);
            ClassDesc returnType = function.getMethodTypeDesc().returnType();
            return DataType.of(returnType, function.isReturnTypeNullable());
        }

        if (caller instanceof ClassIdentifier) {
            return DataType.ofNonNull(resolveConstructor(environment).getClassDesc());
        }

        throw new RuntimeException("Unknown caller TODO" + caller.getClass().getName());
    }

    private ResolvedCallable resolveFunction(Environment environment, ProgramUnit parent) {
        FunctionEnvironment functionEnvironment = resolveMeazyFunction(environment, parent);
        if (functionEnvironment == null) {
            throw new RuntimeException("Can't find function for " + caller.getId() + " and args " + args);
        }

        String className = functionEnvironment.getParent().getFullClassName();
        if (className == null) throw new RuntimeException("Invalid function's parent");

        Expression target;
        if (parent instanceof MemberExpression memberExpression) {
             target = memberExpression.getObject() instanceof ClassIdentifier ? null : memberExpression.getObject();
        }
        else if (functionEnvironment.getModifiers().contains(Modifiers.SHARED()) || functionEnvironment.getParent() instanceof FileEnvironment) {
            target = null;
        }
        else {
            target = new ThisLiteral();
        }

        DataType returnDataType = functionEnvironment.getReturnDataType();

        return new ResolvedCallable(
                ClassDesc.of(className),
                MethodTypeDesc.of(
                        returnDataType == null ? ConstantDescs.CD_void : returnDataType.getClassDesc(),
                        functionEnvironment.getParameters().stream().map(p -> p.getDataType().getClassDesc()).toList()
                ),
                returnDataType != null && returnDataType.isNullable(),
                target,
                functionEnvironment.getParent() instanceof ClassEnvironment classEnvironment && classEnvironment.isInterface()
        );
    }

    @Nullable
    private FunctionEnvironment resolveMeazyFunction(Environment environment, ProgramUnit parent) {
        String id = caller.getId();
        List<DataType> args = this.args.stream().map(arg -> arg.getType(environment, this)).toList();

        if (parent instanceof MemberExpression memberExpression) {
            ClassDesc classDesc = memberExpression.getObject().getType(environment, this).getClassDesc();

            ClassEnvironment classEnvironment = EnvironmentUtils.getClass(environment, classDesc).orElse(null);
            if (classEnvironment == null) return null;

            return classEnvironment.getFunctionRecursively(id, args).orElse(null);
        }

        return EnvironmentUtilsKt.getFunction(environment, id, args);
    }



    private ResolvedCallable resolveConstructor(Environment environment) {
        ConstructorEnvironment constructorEnvironment = resolveMeazyConstructor(environment);
        if (constructorEnvironment == null) throw new RuntimeException("Can't find constructor for " + caller.getId());

        if (!(constructorEnvironment.getParent() instanceof ClassEnvironment classEnvironment)) {
            throw new RuntimeException("Invalid constructor");
        }

        if (classEnvironment.hasModifier(Modifiers.ABSTRACT())) {
            throw new RuntimeException("Can't create instance of abstract class " + classEnvironment.getId());
        }

        List<ClassDesc> parameters = constructorEnvironment.getParameters().stream().map(p -> p.getDataType().getClassDesc()).toList();

        return new ResolvedCallable(
                ClassDesc.of(classEnvironment.getFullClassName()),
                MethodTypeDesc.of(ConstantDescs.CD_void, parameters),
                false,
                null,
                false
        );
    }

    @Nullable
    private ConstructorEnvironment resolveMeazyConstructor(Environment environment) {
        String id = caller.getId();
        List<DataType> args = this.args.stream().map(arg -> arg.getType(environment, this)).toList();

        ClassEnvironment classEnvironment = EnvironmentUtils.getClass(environment, EnvironmentUtils.resolveClassDesc(environment, id, false)).orElse(null);
        if (classEnvironment == null) return null;

        return classEnvironment.getConstructor(args).orElse(null);
    }

    @Override
    public boolean alwaysReturns() {
        return false;
    }



    @Getter
    @AllArgsConstructor
    private static class ResolvedCallable {
        private final ClassDesc classDesc;
        private final MethodTypeDesc methodTypeDesc;
        private final boolean returnTypeNullable;
        @Nullable
        private final Expression target;
        private final boolean isInterface;
    }
}