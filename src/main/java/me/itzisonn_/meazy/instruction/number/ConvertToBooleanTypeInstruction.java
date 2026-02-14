package me.itzisonn_.meazy.instruction.number;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import me.itzisonn_.meazy.instruction.Instruction;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.CodeBuilder;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;

@NullMarked
@AllArgsConstructor
public final class ConvertToBooleanTypeInstruction implements Instruction {
    private final boolean isFromBoxed;
    private final boolean isToBoxed;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        CodeBuilder codeBuilder = bytecodeBuilders.getCodeBuilder();
        if (codeBuilder == null) throw new RuntimeException("Code builder is null");

        if (isFromBoxed == isToBoxed) return;

        if (isFromBoxed) {
            codeBuilder.invokevirtual(ConstantDescs.CD_Boolean, "booleanValue", MethodTypeDesc.of(ConstantDescs.CD_boolean));
        }
        else {
            codeBuilder.invokestatic(ConstantDescs.CD_Boolean, "valueOf", MethodTypeDesc.of(ConstantDescs.CD_Boolean, ConstantDescs.CD_boolean));
        }
    }
}
