package me.itzisonn_.meazy.parser.ast.expression.literal;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ClassDesc;

@NullMarked
public class ThisLiteral implements Expression {
    @Override
    public void emit(InstructionsSet instructions, Environment environment, ProgramUnit parent) {
        instructions.loadThisReference();
    }

    @Override
    public DataType getType(Environment environment, ProgramUnit parent) {
        ClassEnvironment classEnvironment = EnvironmentUtils.getParentOrSelf(environment, ClassEnvironment.class).orElseThrow(
                () -> new IllegalArgumentException("Parent environment for THIS expression must be ClassEnvironment")
        );

        String className = classEnvironment.getFullClassName();
        return DataType.ofNonNull(ClassDesc.of(className));
    }
}
