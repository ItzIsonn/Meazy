package me.itzisonn_.meazy.instruction.field;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.Instruction;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.ClassBuilder;
import java.lang.constant.ClassDesc;

@NullMarked
@AllArgsConstructor
public final class WithFieldInstruction implements Instruction {
    private final String id;
    private final ClassDesc type;
    private final int flags;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        ClassBuilder classBuilder = bytecodeBuilders.getClassBuilder();
        if (classBuilder == null) throw new RuntimeException("Class builder is null");
        classBuilder.withField(id, type, flags);
    }
}
