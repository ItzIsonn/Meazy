package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.util.MiscUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.util.Optional;

@Getter
@NullMarked
public class ReturnStatement implements LocalStatement {
    @Nullable
    private final Expression value;

    public ReturnStatement(@Nullable Expression value) {
        this.value = value;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, ProgramUnit parent) {
        Optional<ConstructorEnvironment> optionalConstructorEnvironment = EnvironmentUtils.getParentOrSelf(environment, ConstructorEnvironment.class);
        if (optionalConstructorEnvironment.isPresent()) {
            if (value != null) throw new RuntimeException("Constructor can't return value TODO");
            instructionsSet.returnVoid();
            return;
        }

        FunctionEnvironment functionEnvironment = EnvironmentUtils.getParentOrSelf(environment, FunctionEnvironment.class).orElseThrow(
                () -> new IllegalArgumentException("Parent environment for RETURN statement must be FunctionEnvironment TODO")
        );

        DataType returnDataType = functionEnvironment.getReturnDataType();

        if (value == null) {
            if (returnDataType != null) {
                throw new RuntimeException("Function must return value TODO");
            }

            instructionsSet.returnVoid();
            return;
        }

        if (returnDataType == null) {
            throw new RuntimeException("Function must not return value TODO");
        }

        value.emit(instructionsSet, environment, this);
        ClassDesc valueClassDesc = value.getType(environment, this).getClassDesc();
        ClassDesc returnTypeClassDesc = returnDataType.getClassDesc();

        if (!EnvironmentUtils.isInstanceOf(functionEnvironment, valueClassDesc, returnTypeClassDesc)) {
            if (!MiscUtils.convertPrimitiveOrBoxed(instructionsSet, valueClassDesc, returnTypeClassDesc)) {
                throw new RuntimeException("Function's return value not matches its return data type TODO");
            }
        }

        instructionsSet.returnValue(returnTypeClassDesc);
    }

    @Override
    public boolean alwaysReturns() {
        return true;
    }
}
