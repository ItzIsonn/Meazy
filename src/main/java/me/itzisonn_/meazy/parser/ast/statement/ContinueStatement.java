package me.itzisonn_.meazy.parser.ast.statement;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ContinueStatement implements Statement {
    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {
        LoopEnvironment loopEnvironment = EnvironmentUtils.getParentOrSelf(environment, LoopEnvironment.class).orElseThrow(
                () -> new IllegalArgumentException("Parent environment for CONTINUE statement must be LoopEnvironment TODO")
        );

        instructionsSet.gotoLabel(loopEnvironment.getStartLabel());
    }
}
