package me.itzisonn_.meazy.parser.ast.expression.collection_creation;

import lombok.Getter;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.util.List;
import java.util.Map;

@Getter
@NullMarked
public class MapCreationExpression implements Expression {
    private final Map<Expression, Expression> map;

    public MapCreationExpression(Map<Expression, Expression> map) {
        this.map = map;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {
        instructionsSet.invokeConstructor(
                ClassDesc.of("java.util.HashMap"),
                MethodTypeDesc.of(ConstantDescs.CD_void, List.of(ClassDesc.of("java.util.Map"))),
                argsInstructions -> hashMapArgsInstructions(argsInstructions, environment)
        );
    }

    @Override
    public DataType getType(Environment environment, Statement parent) {
        return DataType.ofNonNull(ClassDesc.of("java.util.HashMap"));
    }

    private void hashMapArgsInstructions(InstructionsSet instructionsSet, Environment environment) {
        instructionsSet.invokeMethod(
                ConstantDescs.CD_Map,
                "ofEntries",
                MethodTypeDesc.of(ConstantDescs.CD_Map, ClassDesc.of("java.util.Map$Entry").arrayType()),
                argsInstructions -> ofEntriesArgsInstructions(argsInstructions, environment),
                InvokeType.STATIC_INTERFACE
        );
    }

    private void ofEntriesArgsInstructions(InstructionsSet instructionsSet, Environment environment) {
        instructionsSet.loadConstant(map.size());
        instructionsSet.newReferenceArray(ClassDesc.of("java.util.Map$Entry"));

        int i = 0;
        for (Expression key : map.keySet()) {
            Expression value = map.get(key);
            int finalI = i;

            instructionsSet.invokeMethod(
                    ClassDesc.of("java.util.Map"),
                    "entry",
                    MethodTypeDesc.of(ClassDesc.of("java.util.Map$Entry"), ConstantDescs.CD_Object, ConstantDescs.CD_Object),
                    argsInstructions -> {
                        argsInstructions.duplicate();
                        argsInstructions.loadConstant(finalI);

                        key.emit(argsInstructions, environment, this);
                        value.emit(argsInstructions, environment, this);
                    },
                    InvokeType.STATIC_INTERFACE
            );

            instructionsSet.storeReferenceIntoArray();
            i++;
        }
    }
}
