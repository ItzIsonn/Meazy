package me.itzisonn_.meazy.instruction.misc;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.classfile.CodeBuilder;
import java.lang.classfile.TypeKind;
import java.lang.constant.ClassDesc;

@NullMarked
@AllArgsConstructor
public final class ReturnInstruction implements Instruction {
    @Nullable
    private final ClassDesc type;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        CodeBuilder codeBuilder = bytecodeBuilders.getCodeBuilder();
        if (codeBuilder == null) throw new RuntimeException("Code builder is null");

        if (type != null) codeBuilder.return_(TypeKind.from(type));
        else codeBuilder.return_();
    }
}
