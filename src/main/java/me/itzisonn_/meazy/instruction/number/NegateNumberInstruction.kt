package me.itzisonn_.meazy.instruction.number;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.NumberType;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.CodeBuilder;

@NullMarked
@AllArgsConstructor
public final class NegateNumberInstruction implements Instruction {
    private final NumberType type;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        CodeBuilder codeBuilder = bytecodeBuilders.codeBuilder;
        if (codeBuilder == null) throw new RuntimeException("Code builder is null");

        switch (type) {
            case INT -> codeBuilder.ineg();
            case LONG -> codeBuilder.lneg();
            case FLOAT -> codeBuilder.fneg();
            case DOUBLE -> codeBuilder.dneg();
        }
    }
}
