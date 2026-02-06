package me.itzisonn_.meazy.instruction.misc;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.CodeBuilder;
import java.lang.constant.ClassDesc;

@NullMarked
@AllArgsConstructor
public final class CheckCastInstruction implements Instruction {
    private final ClassDesc type;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        CodeBuilder codeBuilder = bytecodeBuilders.getCodeBuilder();
        if (codeBuilder == null) throw new RuntimeException("Code builder is null");
        codeBuilder.checkcast(type);
    }
}
