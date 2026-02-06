package me.itzisonn_.meazy.instruction.local;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.CodeBuilder;
import java.lang.classfile.Label;
import java.lang.constant.ClassDesc;
import java.util.function.Supplier;

@NullMarked
@AllArgsConstructor
public final class SetLocalNameInstruction implements Instruction {
    private final int slot;
    private final String id;
    private final ClassDesc type;
    private final Supplier<Label> startSupplier;
    private final Supplier<Label> endSupplier;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        CodeBuilder codeBuilder = bytecodeBuilders.getCodeBuilder();
        if (codeBuilder == null) throw new RuntimeException("Code builder is null");
        codeBuilder.localVariable(slot, id, type, startSupplier.get(), endSupplier.get());
    }
}
