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
public final class InvokeConstructorInstruction implements Instruction {
    private final ClassDesc owner;
    private final MethodTypeDesc constructorTypeDesc;
    private final Consumer<InstructionsSet> argsInstructions;
    private final boolean isSuper;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        CodeBuilder codeBuilder = bytecodeBuilders.getCodeBuilder();
        if (codeBuilder == null) throw new RuntimeException("Code builder is null");

        if (!isSuper) {
            codeBuilder.new_(owner);
            codeBuilder.dup();
        }

        InstructionsSet instructionsSet = new InstructionsSet(bytecodeBuilders);
        argsInstructions.accept(instructionsSet);

        for (Instruction instruction : instructionsSet.getInstructions()) {
            instruction.emit(bytecodeBuilders);
        }

        codeBuilder.invokespecial(owner, "<init>", constructorTypeDesc);
    }
}
