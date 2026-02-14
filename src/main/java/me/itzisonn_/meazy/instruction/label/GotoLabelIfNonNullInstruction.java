package me.itzisonn_.meazy.instruction.label;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.CodeBuilder;
import java.util.UUID;

@NullMarked
@AllArgsConstructor
public final class GotoLabelIfNonNullInstruction implements Instruction {
    private final UUID uuid;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        CodeBuilder codeBuilder = bytecodeBuilders.getCodeBuilder();
        if (codeBuilder == null) throw new RuntimeException("Code builder is null");
        codeBuilder.ifnonnull(bytecodeBuilders.getLabel(uuid));
    }
}
