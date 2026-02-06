package me.itzisonn_.meazy.instruction.number;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.CodeBuilder;

@NullMarked
@AllArgsConstructor
public final class LogicalOperationInstruction implements Instruction {
    private final LogicalOperation operation;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        CodeBuilder codeBuilder = bytecodeBuilders.getCodeBuilder();
        if (codeBuilder == null) throw new RuntimeException("Code builder is null");

        switch (operation) {
            case OR -> codeBuilder.ior();
            case AND -> codeBuilder.iand();
        }
    }

    public enum LogicalOperation {
        OR,
        AND
    }
}
