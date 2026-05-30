package me.itzisonn_.meazy.parser.ast.statement;

import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ContinueStatement implements LocalStatement {
    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, ProgramUnit parent) {
        LoopEnvironment loopEnvironment = EnvironmentUtils.getParentOrSelf(environment, LoopEnvironment.class).orElseThrow(
                () -> new IllegalArgumentException("Parent environment for CONTINUE statement must be LoopEnvironment TODO")
        );

        instructionsSet.gotoLabel(loopEnvironment.getStartLabel());
    }

    @Override
    public boolean alwaysReturns() {
        return false;
    }
}
