package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.NumberType;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.util.MiscUtils;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;

@Getter
@NullMarked
public class ReturnStatement implements Statement {
    @Nullable
    private final Expression value;

    public ReturnStatement(@Nullable Expression value) {
        this.value = value;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {
        FunctionEnvironment functionEnvironment = EnvironmentUtils.getParentOrSelf(environment, FunctionEnvironment.class).orElseThrow(
                () -> new IllegalArgumentException("Parent environment for RETURN statement must be FunctionEnvironment TODO")
        );

        if (value == null) {
            if (functionEnvironment.getReturnDataType() != null) {
                throw new IllegalArgumentException("Function must not return value TODO");
            }

            instructionsSet.returnVoid();
            return;
        }

        if (functionEnvironment.getReturnDataType() == null) {
            throw new IllegalArgumentException("Function must return value TODO");
        }

        value.emit(instructionsSet, environment, this);
        ClassDesc valueType = value.getType(environment, this).getClassDesc();
        ClassDesc returnType = functionEnvironment.getReturnDataType() == null ? null : functionEnvironment.getReturnDataType().getClassDesc();

        if (!valueType.equals(returnType) && returnType != null) {
            if (NumberType.isNumberType(returnType) && NumberType.isNumberType(valueType)) {
                instructionsSet.convertToNumberType(valueType, returnType);
            }

            else if (MiscUtils.isBoolean(returnType) && MiscUtils.isBoolean(valueType)) {
                instructionsSet.convertToBooleanType(valueType.isClassOrInterface(), returnType.isClassOrInterface());
            }
        }

        instructionsSet.returnValue(returnType);
    }
}
