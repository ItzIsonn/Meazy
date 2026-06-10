package me.itzisonn_.meazy.parser.ast.expression;

import kotlin.Unit;
import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils;
import me.itzisonn_.meazy.util.MiscUtils;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;

@Getter
@NullMarked
public class IsExpression implements Expression {
    private final Expression value;
    private final String dataType;
    private final boolean isLike;

    public IsExpression(Expression value, String dataType, boolean isLike) {
        this.value = value;
        this.dataType = dataType;
        this.isLike = isLike;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, ProgramUnit parent) {
        ClassDesc classDesc = EnvironmentUtils.resolveClassDesc(environment, dataType, false);
        ClassDesc valueClassDesc = value.getType(environment, this).getClassDesc();

        value.emit(instructionsSet, environment, this);
        if (valueClassDesc.isPrimitive()) MiscUtils.boxPrimitive(instructionsSet, valueClassDesc);

        if (isLike) {
            instructionsSet.instanceOf(classDesc);
            return;
        }

        instructionsSet.invokeMethod(
                ConstantDescs.CD_Object,
                "getClass",
                MethodTypeDesc.of(ConstantDescs.CD_Class),
                _ -> Unit.INSTANCE,
                InvokeType.VIRTUAL
        );

        instructionsSet.invokeMethod(
                ConstantDescs.CD_Object,
                "equals",
                MethodTypeDesc.of(ConstantDescs.CD_boolean, ConstantDescs.CD_Object),
                argsInstructions -> {
                    argsInstructions.loadConstant(classDesc);
                    return Unit.INSTANCE;
                },
                InvokeType.VIRTUAL
        );
    }

    @Override
    public DataType getType(Environment environment, ProgramUnit parent) {
        return DataType.ofNonNull(ConstantDescs.CD_boolean);
    }
}