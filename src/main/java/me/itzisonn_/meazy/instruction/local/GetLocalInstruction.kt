package me.itzisonn_.meazy.instruction.local;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.CodeBuilder;
import java.lang.classfile.TypeKind;
import java.lang.constant.ClassDesc;

@NullMarked
@AllArgsConstructor
public final class GetLocalInstruction implements Instruction {
    private final ClassDesc type;
    private final int slot;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        CodeBuilder codeBuilder = bytecodeBuilders.getCodeBuilder();
        if (codeBuilder == null) throw new RuntimeException("Code builder is null");
        codeBuilder.loadLocal(TypeKind.from(type), slot);
    }
}
