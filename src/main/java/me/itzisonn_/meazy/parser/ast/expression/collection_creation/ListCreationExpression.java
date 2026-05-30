package me.itzisonn_.meazy.parser.ast.expression.collection_creation;

import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.util.List;

@Getter
@NullMarked
public class ListCreationExpression implements Expression {
    private final List<Expression> list;

    public ListCreationExpression(List<Expression> list) {
        this.list = list;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, ProgramUnit parent) {
        instructionsSet.invokeConstructor(
                ClassDesc.of("java.util.ArrayList"),
                MethodTypeDesc.of(ConstantDescs.CD_void, List.of(ClassDesc.of("java.util.Collection"))),
                argsInstructions -> arrayListArgsInstructions(argsInstructions, environment)
        );
    }

    @Override
    public DataType getType(Environment environment, ProgramUnit parent) {
        return DataType.ofNonNull(ClassDesc.of("java.util.ArrayList"));
    }

    private void arrayListArgsInstructions(InstructionsSet instructionsSet, Environment environment) {
        instructionsSet.invokeMethod(
                ConstantDescs.CD_List,
                "of",
                MethodTypeDesc.of(ConstantDescs.CD_List, ConstantDescs.CD_Object.arrayType()),
                argsInstructions -> ofArgsInstructions(argsInstructions, environment),
                InvokeType.STATIC_INTERFACE
        );
    }

    private void ofArgsInstructions(InstructionsSet instructionsSet, Environment environment) {
        instructionsSet.loadConstant(list.size());
        instructionsSet.newReferenceArray(list.isEmpty() ? ConstantDescs.CD_Object : list.getFirst().getType(environment, this).getClassDesc());

        for (int i = 0; i < list.size(); i++) {
            Expression arg = list.get(i);
            instructionsSet.duplicate();
            instructionsSet.loadConstant(i);
            arg.emit(instructionsSet, environment, this);
            instructionsSet.storeReferenceIntoArray();
        }
    }
}
