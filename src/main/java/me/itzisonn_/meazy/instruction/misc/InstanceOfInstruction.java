package me.itzisonn_.meazy.instruction.misc;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.CodeBuilder;
import java.lang.constant.ClassDesc;

@NullMarked
@AllArgsConstructor
public final class InstanceOfInstruction implements Instruction {
    private final ClassDesc target;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        CodeBuilder codeBuilder = bytecodeBuilders.codeBuilder;
        if (codeBuilder == null) throw new RuntimeException("Code builder is null");
        codeBuilder.instanceOf(target);
    }
}
