package me.itzisonn_.meazy.instruction.conditional_goto;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.CodeBuilder;
import java.lang.classfile.Label;
import java.util.function.Supplier;

@NullMarked
@AllArgsConstructor
public final class GotoLabelIfNonNullInstruction implements Instruction {
    private final Supplier<Label> supplier;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        CodeBuilder codeBuilder = bytecodeBuilders.getCodeBuilder();
        if (codeBuilder == null) throw new RuntimeException("Code builder is null");
        codeBuilder.ifnonnull(supplier.get());
    }
}
