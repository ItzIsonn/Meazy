package me.itzisonn_.meazy.parser.ast.expression.literal;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.modifier.Modifiers;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ClassDesc;

@NullMarked
public class ThisLiteral implements Expression {
    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {
        instructionsSet.loadThisReference();
    }

    @Override
    public DataType getType(Environment environment, Statement parent) {
        ClassEnvironment classEnvironment = EnvironmentUtils.getParentOrSelf(environment, ClassEnvironment.class).orElseThrow(
                () -> new IllegalArgumentException("Parent environment for THIS expression must be ClassEnvironment")
        );

        String className;
        if (classEnvironment.getModifiers().contains(Modifiers.OPEN())) {
            className = EnvironmentUtils.getPackageName(classEnvironment).orElseThrow() + "." + classEnvironment.getId();
        }
        else {
            className = EnvironmentUtils.getPackageName(classEnvironment).orElseThrow() + "." + EnvironmentUtils.getFileEnvironment(classEnvironment).orElseThrow().getClassName()  + "$" + classEnvironment.getId();
        }

        return DataType.ofNonNull(ClassDesc.of(className));
    }
}
