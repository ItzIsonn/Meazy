package me.itzisonn_.meazy.parser.ast.expression;

import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.NumberType;
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.constantpool.ConstantPoolBuilder;
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
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {
        ClassDesc classDesc = EnvironmentUtils.resolveClassDesc(environment, dataType);

        if (isLike) {
            value.emit(instructionsSet, environment, this);
            instructionsSet.instanceOf(classDesc);
            return;
        }

        value.emit(instructionsSet, environment, this);
        ClassDesc valueType = value.getType(environment, this).getClassDesc();

        if (NumberType.isNumberType(valueType)) {
            NumberType valueNumberType = NumberType.valueOf(classDesc);

            if (valueNumberType != null && !valueNumberType.isBoxed()) {
                instructionsSet.convertToNumberType(valueNumberType, valueNumberType.box());
            }
        }
        else if (valueType.equals(ConstantDescs.CD_boolean)) {
            instructionsSet.convertToBooleanType(false, true);
        }

        instructionsSet.invokeMethod(
                ConstantDescs.CD_Object,
                "getClass",
                MethodTypeDesc.of(ConstantDescs.CD_Class),
                _ -> {},
                InvokeType.VIRTUAL
        );

        instructionsSet.invokeMethod(
                ConstantDescs.CD_Object,
                "equals",
                MethodTypeDesc.of(ConstantDescs.CD_boolean, ConstantDescs.CD_Object),
                argsInstructions -> argsInstructions.loadConstant(ConstantPoolBuilder.of().classEntry(classDesc).constantValue()),
                InvokeType.VIRTUAL
        );
    }

    @Override
    public DataType getType(Environment environment, Statement parent) {
        return DataType.ofNonNull(ConstantDescs.CD_boolean);
    }
}