package me.itzisonn_.meazy.instruction.method;

import lombok.AllArgsConstructor;
import me.itzisonn_.meazy.instruction.BytecodeBuilders;
import me.itzisonn_.meazy.instruction.Instruction;
import org.jspecify.annotations.NullMarked;

import java.lang.classfile.CodeBuilder;
import java.lang.constant.ConstantDesc;
import java.lang.constant.DirectMethodHandleDesc;
import java.lang.constant.DynamicCallSiteDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.List;

@NullMarked
@AllArgsConstructor
public final class InvokeDynamicMethodInstruction implements Instruction {
    private final DirectMethodHandleDesc bootstrapMethod;
    private final String id;
    private final MethodTypeDesc methodTypeDesc;
    private final List<ConstantDesc> args;

    @Override
    public void emit(BytecodeBuilders bytecodeBuilders) {
        CodeBuilder codeBuilder = bytecodeBuilders.getCodeBuilder();
        if (codeBuilder == null) throw new RuntimeException("Code builder is null");
        codeBuilder.invokedynamic(DynamicCallSiteDesc.of(bootstrapMethod, id, methodTypeDesc, args.toArray(new ConstantDesc[0])));
    }
}
