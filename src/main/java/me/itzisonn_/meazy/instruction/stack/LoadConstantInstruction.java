package me.itzisonn_.meazy.instruction.stack;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.classfile.CodeBuilder;
import java.lang.constant.ConstantDesc;

@NullMarked
@AllArgsConstructor
public final class LoadConstantInstruction implements Instruction {
    @Nullable
    private final ConstantDesc constant;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        CodeBuilder codeBuilder = bytecodeBuilders.codeBuilder;
        if (codeBuilder == null) throw new RuntimeException("Code builder is null");

        if (constant != null) codeBuilder.loadConstant(constant);
        else codeBuilder.aconst_null();
    }
}
