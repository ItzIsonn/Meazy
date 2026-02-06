package me.itzisonn_.meazy.instruction.number;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.NumberType;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.CodeBuilder;

@NullMarked
@AllArgsConstructor
public final class ArithmeticOperationInstruction implements Instruction {
    private final NumberType type;
    private final ArithmeticOperation operation;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        CodeBuilder codeBuilder = bytecodeBuilders.getCodeBuilder();
        if (codeBuilder == null) throw new RuntimeException("Code builder is null");

        switch (type) {
            case INT -> {
                switch (operation) {
                    case ADDITION -> codeBuilder.iadd();
                    case SUBTRACTION -> codeBuilder.isub();
                    case MULTIPLICATION -> codeBuilder.imul();
                    case DIVISION -> codeBuilder.idiv();
                    case REMAINDER -> codeBuilder.irem();
                }
            }

            case LONG -> {
                switch (operation) {
                    case ADDITION -> codeBuilder.ladd();
                    case SUBTRACTION -> codeBuilder.lsub();
                    case MULTIPLICATION -> codeBuilder.lmul();
                    case DIVISION -> codeBuilder.ldiv();
                    case REMAINDER -> codeBuilder.lrem();
                }
            }

            case FLOAT -> {
                switch (operation) {
                    case ADDITION -> codeBuilder.fadd();
                    case SUBTRACTION -> codeBuilder.fsub();
                    case MULTIPLICATION -> codeBuilder.fmul();
                    case DIVISION -> codeBuilder.fdiv();
                    case REMAINDER -> codeBuilder.frem();
                }
            }

            case DOUBLE -> {
                switch (operation) {
                    case ADDITION -> codeBuilder.dadd();
                    case SUBTRACTION -> codeBuilder.dsub();
                    case MULTIPLICATION -> codeBuilder.dmul();
                    case DIVISION -> codeBuilder.ddiv();
                    case REMAINDER -> codeBuilder.drem();
                }
            }
        }
    }

    public enum ArithmeticOperation {
        ADDITION,
        SUBTRACTION,
        MULTIPLICATION,
        DIVISION,
        REMAINDER
    }
}
