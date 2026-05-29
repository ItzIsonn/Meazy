package me.itzisonn_.meazy.parser.ast.expression;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.NumberType;
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.*;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy.runtime.value.ConstructorValue;
import me.itzisonn_.meazy.runtime.value.FunctionValue;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy.parser.ast.expression.literal.ThisLiteral;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.util.List;
import java.util.UUID;

@Getter
@NullMarked
public class CallExpression implements Expression {
    private final Identifier caller;
    private final List<Expression> args;

    public CallExpression(Identifier caller, List<Expression> args) {
        this.caller = caller;
        this.args = args;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {
        if (caller instanceof FunctionIdentifier) {
            ResolvedCallable resolvedFunction = resolveFunction(environment, parent);
            UUID endLabel = null;

            if (resolvedFunction.getTarget() != null) {
                resolvedFunction.getTarget().emit(instructionsSet, environment, this);

                if (parent instanceof MemberExpression memberExpression && memberExpression.isNullSafe()) {
                    UUID nonnullLabel = instructionsSet.createAndInitLabel();
                    endLabel = instructionsSet.createAndInitLabel();

                    instructionsSet.duplicate();
                    instructionsSet.gotoLabelIfNonNull(nonnullLabel);

                    instructionsSet.pop();
                    instructionsSet.loadNull();
                    instructionsSet.gotoLabel(endLabel);

                    instructionsSet.bindLabel(nonnullLabel);
                }
            }

            instructionsSet.invokeMethod(
                    resolvedFunction.getClassDesc(),
                    caller.getId(),
                    resolvedFunction.getMethodTypeDesc(),
                    argsInstructions -> {
                        for (int i = 0; i < args.size(); i++) {
                            ClassDesc parameterType = resolvedFunction.getMethodTypeDesc().parameterType(i);

                            Expression arg = args.get(i);
                            ClassDesc argType = arg.getType(environment, this).getClassDesc();

                            if (!argType.equals(parameterType)) {
                                NumberType argNumberType = NumberType.valueOf(argType);

                                if (argNumberType != null && !argNumberType.isBoxed() && parameterType.isClassOrInterface()) {
                                    argsInstructions.convertToNumberType(argNumberType, argNumberType.box());
                                }
                            }

                            arg.emit(argsInstructions, environment, this);
                        }
                    },
                    resolvedFunction.getTarget() == null ?
                            resolvedFunction.isInterface() ? InvokeType.STATIC_INTERFACE : InvokeType.STATIC :
                            resolvedFunction.isInterface() ? InvokeType.INTERFACE : InvokeType.VIRTUAL
            );

//            if (resolvedFunction.getMethodTypeDesc().returnType().isPrimitive()) {
//                MiscUtils.boxPrimitive(instructionsSet, resolvedFunction.getMethodTypeDesc().returnType());
//            }

            if (endLabel != null) {
                instructionsSet.bindLabel(endLabel);
            }
        }

        else if (caller instanceof ClassIdentifier) {
            ResolvedCallable resolvedConstructor = resolveConstructor(environment);

            instructionsSet.invokeConstructor(
                    resolvedConstructor.getClassDesc(),
                    resolvedConstructor.getMethodTypeDesc(),
                    argsInstructions -> {
                        for (Expression arg : args) {
                            arg.emit(argsInstructions, environment, this);
                        }
                    }
            );
        }

        else throw new RuntimeException("Unknown caller TODO " + caller.getClass().getName());
    }

    @Override
    public DataType getType(Environment environment, Statement parent) {
        if (caller instanceof FunctionIdentifier) {
            ClassDesc returnType = resolveFunction(environment, parent).getMethodTypeDesc().returnType();
            return DataType.of(returnType, !returnType.isPrimitive());
        }

        if (caller instanceof ClassIdentifier) {
            return DataType.ofNonNull(resolveConstructor(environment).getClassDesc());
        }

        throw new RuntimeException("Unknown caller TODO" + caller.getClass().getName());
    }

    private ResolvedCallable resolveFunction(Environment environment, Statement parent) {
        FunctionValue functionValue = resolveMeazyFunction(environment, parent);
        if (functionValue == null) throw new RuntimeException("Can't find function for " + caller.getId());

        String className;
        if (functionValue.getEnvironment().getParent() instanceof ClassEnvironment classEnvironment) {
            className = EnvironmentUtils.getPackageName(classEnvironment).orElseThrow() + "." + classEnvironment.getId();
        }
        else {
            className = EnvironmentUtils.getPackageName(functionValue.getEnvironment()).orElseThrow() + "." + EnvironmentUtils.getClassName(functionValue.getEnvironment()).orElseThrow();
        }

        DataType returnDataType = functionValue.getReturnDataType();

        Expression target;
        if (parent instanceof MemberExpression memberExpression) {
             target = memberExpression.getObject() instanceof ClassIdentifier ? null : memberExpression.getObject();
        }
        else if (functionValue.getModifiers().contains(Modifiers.SHARED()) || functionValue.getEnvironment().getParent() instanceof FileEnvironment) {
            target = null;
        }
        else {
            target = new ThisLiteral();
        }

        return new ResolvedCallable(
                ClassDesc.of(className),
                MethodTypeDesc.of(
                        returnDataType == null ? ConstantDescs.CD_void : returnDataType.getClassDesc(),
                        functionValue.getParameters().stream().map(p -> p.getDataType().getClassDesc()).toList()
                ),
                target,
                functionValue.getEnvironment().getParent() instanceof ClassEnvironment classEnvironment && classEnvironment.isInterface()
        );
    }

    @Nullable
    private FunctionValue resolveMeazyFunction(Environment environment, Statement parent) {
        String id = caller.getId();
        List<ClassDesc> parameters = args.stream().map(arg -> arg.getType(environment, this).getClassDesc()).toList();

        if (parent instanceof MemberExpression memberExpression) {
            ClassDesc classDesc = memberExpression.getObject().getType(environment, this).getClassDesc();

            ClassValue classValue = EnvironmentUtils.getClassValue(environment, classDesc).orElse(null);
            if (classValue == null) return null;

            return classValue.getEnvironment().getFunction(id, parameters).orElse(null);
        }

        return EnvironmentUtils.getFunctionValue(environment, id, parameters).orElse(null);
    }



    private ResolvedCallable resolveConstructor(Environment environment) {
        ConstructorValue constructorValue = resolveMeazyConstructor(environment);
        if (constructorValue == null) throw new RuntimeException("Can't find constructor for " + caller.getId());

        String className;
        if (constructorValue.getEnvironment().getParent() instanceof ClassEnvironment classEnvironment) {
            className = EnvironmentUtils.getPackageName(classEnvironment).orElseThrow() + "." + classEnvironment.getId();
        }
        else throw new RuntimeException("Invalid constructor");

        List<ClassDesc> parameters = constructorValue.getParameters().stream().map(p -> p.getDataType().getClassDesc()).toList();

        return new ResolvedCallable(
                ClassDesc.of(className),
                MethodTypeDesc.of(ConstantDescs.CD_void, parameters),
                null,
                false
        );
    }

    @Nullable
    private ConstructorValue resolveMeazyConstructor(Environment environment) {
        String id = caller.getId();
        List<ClassDesc> parameters = args.stream().map(arg -> arg.getType(environment, this).getClassDesc()).toList();

        ClassValue classValue = EnvironmentUtils.getClassValue(environment, EnvironmentUtils.resolveClassDesc(environment, id)).orElse(null);
        if (classValue == null) return null;

        return classValue.getEnvironment().getConstructor(parameters).orElse(null);
    }



    @Getter
    @AllArgsConstructor
    private static class ResolvedCallable {
        private final ClassDesc classDesc;
        private final MethodTypeDesc methodTypeDesc;
        @Nullable
        private final Expression target;
        private final boolean isInterface;
    }
}