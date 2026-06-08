package me.itzisonn_.meazy.instruction.method;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.ClassBuilder;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.AccessFlag;
import java.util.function.Consumer;

@NullMarked
@AllArgsConstructor
public final class WithMethodInstruction implements Instruction {
    private final String id;
    private final MethodTypeDesc methodTypeDesc;
    private final int flags;
    private final Consumer<InstructionsSet> bodyInstructions;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        ClassBuilder classBuilder = bytecodeBuilders.getClassBuilder();
        if (classBuilder == null) throw new RuntimeException("Class builder is null");

        if ((AccessFlag.ABSTRACT.mask() & flags) != 0) {
            classBuilder.withMethod(id, methodTypeDesc, flags, _ -> {});
            return;
        }

        classBuilder.withMethodBody(
                id,
                methodTypeDesc,
                flags,
                codeBuilder -> {
                    BytecodeBuilders methodBytecodeBuilders = bytecodeBuilders.copy(codeBuilder);
                    InstructionsSet instructionsSet = new InstructionsSet(methodBytecodeBuilders);
                    bodyInstructions.accept(instructionsSet);

                    for (Instruction instruction : instructionsSet.getInstructions()) {
                        instruction.emit(methodBytecodeBuilders);
                    }
                }
        );
    }
}
