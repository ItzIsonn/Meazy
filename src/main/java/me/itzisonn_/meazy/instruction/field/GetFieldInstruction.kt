package me.itzisonn_.meazy.instruction.field;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.CodeBuilder;
import java.lang.constant.ClassDesc;

@NullMarked
@AllArgsConstructor
public final class GetFieldInstruction implements Instruction {
    private final ClassDesc owner;
    private final String id;
    private final ClassDesc type;
    private final boolean isStatic;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        CodeBuilder codeBuilder = bytecodeBuilders.getCodeBuilder();
        if (codeBuilder == null) throw new RuntimeException("Code builder is null");

        if (isStatic) codeBuilder.getstatic(owner, id, type);
        else codeBuilder.getfield(owner, id, type);
    }
}
