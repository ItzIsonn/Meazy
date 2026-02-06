package me.itzisonn_.meazy.instruction.misc;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.attribute.InnerClassesAttribute;
import java.lang.constant.ClassDesc;
import java.util.List;
import java.util.function.Consumer;

@NullMarked
@AllArgsConstructor
public final class WithClassInstruction implements Instruction {
    private final ClassDesc classDesc;
    private final int flags;
    private final List<InnerClassesAttribute> attributes;
    private final Consumer<InstructionsSet> classInstructions;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        bytecodeBuilders.withClass(
                classDesc,
                classBuilder -> {
                    classBuilder.withFlags(flags);
                    for (var attribute : attributes) classBuilder.with(attribute);

                    BytecodeBuilders classBytecodeBuilders = bytecodeBuilders.copy(classBuilder, null);
                    InstructionsSet instructionsSet = new InstructionsSet(classBytecodeBuilders);
                    classInstructions.accept(instructionsSet);

                    for (Instruction instruction : instructionsSet.getInstructions()) {
                        instruction.emit(classBytecodeBuilders);
                    }
                }
        );
    }
}
