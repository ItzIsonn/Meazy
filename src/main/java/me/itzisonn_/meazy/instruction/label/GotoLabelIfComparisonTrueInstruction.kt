package me.itzisonn_.meazy.instruction.label;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.NumberType;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.CodeBuilder;
import java.lang.classfile.Label;
import java.util.UUID;

@NullMarked
@AllArgsConstructor
public final class GotoLabelIfComparisonTrueInstruction implements Instruction {
    private final NumberType type;
    private final ComparisonOperation operation;
    private final UUID uuid;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        CodeBuilder codeBuilder = bytecodeBuilders.getCodeBuilder();
        if (codeBuilder == null) throw new RuntimeException("Code builder is null");

        Label label = bytecodeBuilders.getLabel(uuid);

        if (type == NumberType.INT) {
            switch (operation) {
                case EQUALS -> codeBuilder.if_icmpeq(label);
                case NOT_EQUALS -> codeBuilder.if_icmpne(label);
                case GREATER -> codeBuilder.if_icmpgt(label);
                case GREATER_OR_EQUALS -> codeBuilder.if_icmpge(label);
                case LESS -> codeBuilder.if_icmplt(label);
                case LESS_OR_EQUALS -> codeBuilder.if_icmple(label);
            }

            return;
        }

        switch (type) {
            case LONG -> codeBuilder.lcmp();
            case FLOAT -> codeBuilder.fcmpl();
            case DOUBLE -> codeBuilder.dcmpl();
        }

        switch (operation) {
            case EQUALS -> codeBuilder.ifeq(label);
            case NOT_EQUALS -> codeBuilder.ifne(label);
            case GREATER -> codeBuilder.ifgt(label);
            case GREATER_OR_EQUALS -> codeBuilder.ifge(label);
            case LESS -> codeBuilder.iflt(label);
            case LESS_OR_EQUALS -> codeBuilder.ifle(label);
        }
    }

    public enum ComparisonOperation {
        EQUALS,
        NOT_EQUALS,
        GREATER,
        GREATER_OR_EQUALS,
        LESS,
        LESS_OR_EQUALS
    }
}
