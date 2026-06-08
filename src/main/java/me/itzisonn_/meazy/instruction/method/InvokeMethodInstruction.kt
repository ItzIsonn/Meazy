package me.itzisonn_.meazy.instruction.method;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.CodeBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.function.Consumer;

@NullMarked
@AllArgsConstructor
public final class InvokeMethodInstruction implements Instruction {
    private final ClassDesc owner;
    private final String id;
    private final MethodTypeDesc methodTypeDesc;
    private final Consumer<InstructionsSet> argsInstructions;
    private final InvokeType invokeType;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        CodeBuilder codeBuilder = bytecodeBuilders.getCodeBuilder();
        if (codeBuilder == null) throw new RuntimeException("Code builder is null");

        InstructionsSet instructionsSet = new InstructionsSet(bytecodeBuilders);
        argsInstructions.accept(instructionsSet);

        for (Instruction instruction : instructionsSet.getInstructions()) {
            instruction.emit(bytecodeBuilders);
        }

        switch (invokeType) {
            case STATIC -> codeBuilder.invokestatic(owner, id, methodTypeDesc, false);
            case STATIC_INTERFACE -> codeBuilder.invokestatic(owner, id, methodTypeDesc, true);
            case INTERFACE -> codeBuilder.invokeinterface(owner, id, methodTypeDesc);
            case VIRTUAL -> codeBuilder.invokevirtual(owner, id, methodTypeDesc);
            case SPECIAL -> codeBuilder.invokespecial(owner, id, methodTypeDesc);
        }
    }

    public enum InvokeType {
        STATIC,
        STATIC_INTERFACE,
        INTERFACE,
        VIRTUAL,
        SPECIAL
    }
}
