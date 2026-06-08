package me.itzisonn_.meazy.parser.ast.statement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.runtime.environment.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.util.List;

@Getter
@NullMarked
public class BaseCallStatement implements LocalStatement {
    protected final List<Expression> args;

    public BaseCallStatement(List<Expression> args) {
        this.args = args;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, ProgramUnit parent) { //TODO add support for automatic base calling before return
        if (!EnvironmentUtils.hasParentOrSelf(environment, ConstructorEnvironment.class)) {
            throw new IllegalArgumentException("Parent environment for BASE statement must be ConstructorEnvironment TODO");
        }

        ResolvedConstructor resolvedConstructor = resolveConstructor(environment);
        instructionsSet.loadThisReference();

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

    @Override
    public boolean alwaysReturns() {
        return false;
    }



    private ResolvedConstructor resolveConstructor(Environment environment) {
        ConstructorEnvironment constructorEnvironment = resolveMeazyConstructor(environment);
        if (constructorEnvironment == null) throw new RuntimeException();

        if (!(constructorEnvironment.getParent() instanceof ClassEnvironment classEnvironment)) {
            throw new RuntimeException("Can't call super class not inside class");
        }

        List<ClassDesc> parameters = constructorEnvironment.getParameters().stream().map(p -> p.getDataType().getClassDesc()).toList();

        return new ResolvedConstructor(
                ClassDesc.of(classEnvironment.getFullClassName()),
                MethodTypeDesc.of(ConstantDescs.CD_void, parameters)
        );
    }

    @Nullable
    private ConstructorEnvironment resolveMeazyConstructor(Environment environment) {
        ClassEnvironment classEnvironment = EnvironmentUtils.getParent(environment, ClassEnvironment.class).orElseThrow(
                () -> new RuntimeException("Can't call super class not inside class")
        );

        ClassDesc baseClassDesc = classEnvironment.getBaseClass();
        if (baseClassDesc == null) return null;

        List<ClassDesc> parameters = args.stream().map(arg -> arg.getType(environment, this).getClassDesc()).toList();

        ClassEnvironment baseClassEnvironment = EnvironmentUtils.getClassEnvironment(environment, baseClassDesc).orElse(null);
        if (baseClassEnvironment == null) return null;

        return baseClassEnvironment.getConstructor(parameters).orElse(null);
    }



    @Getter
    @AllArgsConstructor
    private static class ResolvedConstructor {
        private final ClassDesc classDesc;
        private final MethodTypeDesc methodTypeDesc;
    }
}