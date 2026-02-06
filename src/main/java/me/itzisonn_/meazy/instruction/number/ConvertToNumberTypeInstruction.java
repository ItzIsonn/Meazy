package me.itzisonn_.meazy.instruction.number;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.NumberType;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.CodeBuilder;

@NullMarked
@AllArgsConstructor
public final class ConvertToNumberTypeInstruction implements Instruction {
    private final NumberType from;
    private final NumberType to;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        CodeBuilder codeBuilder = bytecodeBuilders.getCodeBuilder();
        if (codeBuilder == null) throw new RuntimeException("Code builder is null");

        switch (from) {
            case INT -> {
                switch (to) {
                    case INT -> {}
                    case LONG -> codeBuilder.i2l();
                    case FLOAT -> codeBuilder.i2f();
                    case DOUBLE -> codeBuilder.i2d();
                }
            }

            case LONG -> {
                switch (to) {
                    case INT -> codeBuilder.l2i();
                    case LONG -> {}
                    case FLOAT -> codeBuilder.l2f();
                    case DOUBLE -> codeBuilder.l2d();
                }
            }

            case FLOAT -> {
                switch (to) {
                    case INT -> codeBuilder.f2i();
                    case LONG -> codeBuilder.f2l();
                    case FLOAT -> {}
                    case DOUBLE -> codeBuilder.f2d();
                }
            }

            case DOUBLE -> {
                switch (to) {
                    case INT -> codeBuilder.d2i();
                    case LONG -> codeBuilder.d2l();
                    case FLOAT -> codeBuilder.d2f();
                    case DOUBLE -> {}
                }
            }
        }
    }
}
