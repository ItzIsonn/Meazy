package me.itzisonn_.meazy.instruction.number;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.NumberType;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.CodeBuilder;
import java.lang.constant.MethodTypeDesc;

@NullMarked
@AllArgsConstructor
public final class ConvertToNumberTypeInstruction implements Instruction {
    private final NumberType from;
    private final NumberType to;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        CodeBuilder codeBuilder = bytecodeBuilders.getCodeBuilder();
        if (codeBuilder == null) throw new RuntimeException("Code builder is null");

        if (!from.isBoxed() && !to.isBoxed()) emitUnboxed(codeBuilder, from, to);
        else if (from.isBoxed() && !to.isBoxed()) emitBoxedToUnboxed(codeBuilder, from, to);
        else if (!from.isBoxed()) emitUnboxedToBoxed(codeBuilder, from, to);
        else emitBoxed(codeBuilder, from, to);
    }

    private void emitUnboxed(CodeBuilder codeBuilder, NumberType from, NumberType to) {
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

    private void emitBoxedToUnboxed(CodeBuilder codeBuilder, NumberType from, NumberType to) {
        String methodName = switch (to) {
            case INT -> "intValue";
            case LONG -> "longValue";
            case FLOAT -> "floatValue";
            case DOUBLE -> "doubleValue";
            default -> null;
        };

        codeBuilder.invokevirtual(from.getClassDesc(), methodName, MethodTypeDesc.of(to.getClassDesc()));
    }

    private void emitUnboxedToBoxed(CodeBuilder codeBuilder, NumberType from, NumberType to) {
        emitUnboxed(codeBuilder, from, to.unbox());
        codeBuilder.invokestatic(to.getClassDesc(), "valueOf", MethodTypeDesc.of(to.getClassDesc(), to.unbox().getClassDesc()));
    }

    private void emitBoxed(CodeBuilder codeBuilder, NumberType from, NumberType to) {
        emitBoxedToUnboxed(codeBuilder, from, from.unbox());
        emitUnboxedToBoxed(codeBuilder, from.unbox(), to);
    }
}
