package me.itzisonn_.meazy.instruction.label;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.CodeBuilder;
import java.lang.classfile.Label;
import java.util.function.Consumer;

@NullMarked
@AllArgsConstructor
public final class NewLabelInstruction implements Instruction {
    private final Consumer<Label> callback;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        CodeBuilder codeBuilder = bytecodeBuilders.getCodeBuilder();
        if (codeBuilder == null) throw new RuntimeException("Code builder is null");

        Label label = codeBuilder.newLabel();
        callback.accept(label);
    }
}
