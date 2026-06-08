package me.itzisonn_.meazy.instruction.method;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.ClassBuilder;
import java.lang.constant.MethodTypeDesc;
import java.lang.reflect.AccessFlag;
import java.lang.reflect.AccessFlag.Location;
import java.util.function.Consumer;

@NullMarked
@AllArgsConstructor
public final class WithConstructorInstruction implements Instruction {
    private final MethodTypeDesc methodTypeDesc;
    private final int flags;
    private final Consumer<InstructionsSet> bodyInstructions;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        ClassBuilder classBuilder = bytecodeBuilders.getClassBuilder();
        if (classBuilder == null) throw new RuntimeException("Class builder is null");

        boolean isStatic = AccessFlag.maskToAccessFlags(flags, Location.METHOD).contains(AccessFlag.STATIC);

        classBuilder.withMethodBody(
                isStatic ? "<clinit>" : "<init>",
                methodTypeDesc,
                flags,
                codeBuilder -> {
                    BytecodeBuilders constructorBytecodeBuilders = bytecodeBuilders.copy(codeBuilder);
                    InstructionsSet instructionsSet = new InstructionsSet(constructorBytecodeBuilders);
                    bodyInstructions.accept(instructionsSet);

                    for (Instruction instruction : instructionsSet.getInstructions()) {
                        instruction.emit(constructorBytecodeBuilders);
                    }
                }
        );
    }
}
