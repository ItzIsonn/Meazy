package me.itzisonn_.meazy.parser.ast.statement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy.runtime.value.ConstructorValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.util.List;

@Getter
@NullMarked
public class BaseCallStatement implements Statement {
    protected final List<Expression> args;

    public BaseCallStatement(List<Expression> args) {
        this.args = args;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {
        ResolvedConstructor resolvedConstructor = resolveConstructor(environment);

        instructionsSet.invokeSuperClass(
                resolvedConstructor.getClassDesc(),
                resolvedConstructor.getMethodTypeDesc(),
                argsInstructions -> {
                    for (Expression arg : args) {
                        arg.emit(argsInstructions, environment, this);
                    }
                }
        );
    }



    private ResolvedConstructor resolveConstructor(Environment environment) {
        if (!EnvironmentUtils.hasParent(environment, ClassEnvironment.class)) {
            throw new RuntimeException("Can't call super class not inside class"); // TODO
        }

        ConstructorValue constructorValue = resolveMeazyConstructor(environment);
        if (constructorValue == null) throw new RuntimeException();

        String className;
        if (constructorValue.getEnvironment().getParent() instanceof ClassEnvironment classEnvironment) {
            className = EnvironmentUtils.getPackageName(classEnvironment).orElseThrow() + "." + classEnvironment.getId();
        }
        else throw new RuntimeException("Invalid constructor");

        List<ClassDesc> parameters = constructorValue.getParameters().stream().map(p -> p.getDataType().getClassDesc()).toList();

        return new ResolvedConstructor(
                ClassDesc.of(className),
                MethodTypeDesc.of(ConstantDescs.CD_void, parameters)
        );
    }

    @Nullable
    private ConstructorValue resolveMeazyConstructor(Environment environment) {
        ClassEnvironment classEnvironment = EnvironmentUtils.getParent(environment, ClassEnvironment.class).orElseThrow(
                () -> new RuntimeException("Can't call super class not inside class")
        );

        ClassDesc baseClassDesc = classEnvironment.getBaseClass();
        if (baseClassDesc == null) return null;

        List<ClassDesc> parameters = args.stream().map(arg -> arg.getType(environment, this).getClassDesc()).toList();

        ClassValue classValue = EnvironmentUtils.getClassValue(environment, baseClassDesc).orElse(null);
        if (classValue == null) return null;

        return classValue.getEnvironment().getConstructor(parameters).orElse(null);
    }



    @Getter
    @AllArgsConstructor
    private static class ResolvedConstructor {
        private final ClassDesc classDesc;
        private final MethodTypeDesc methodTypeDesc;
    }
}