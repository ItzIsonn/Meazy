package me.itzisonn_.meazy.parser.ast.statement;

import kotlin.Unit;
import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LocalVariableDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment;
import me.itzisonn_.meazy.runtime.VariableValue;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.util.List;

@Getter
@NullMarked
public class ForeachStatement implements LocalStatement {
    private final boolean isConstant;
    private final String id;
    private final DataType dataType;
    private final Expression collection;
    private final List<LocalStatement> body;

    public ForeachStatement(boolean isConstant, String id, DataType dataType, Expression collection, List<LocalStatement> body) {
        this.isConstant = isConstant;
        this.id = id;
        this.dataType = dataType;
        this.collection = collection;
        this.body = body;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, ProgramUnit parent) {
        if (!(environment instanceof LocalVariableDeclarationEnvironment localVariableDeclarationEnvironment)) {
            throw new RuntimeException("Foreach statement must be inside variableDeclarationEnvironment TODO");
        }

        VariableValue iterableVariableValue = localVariableDeclarationEnvironment.declareVariable(
                null,
                DataType.ofNonNull(ClassDesc.of("java.lang.Iterable")),
                true,
                null
        );

        collection.emit(instructionsSet, environment, this);

        instructionsSet.invokeMethod(
                ClassDesc.of("java.lang.Iterable"),
                "iterator", 
                MethodTypeDesc.of(ClassDesc.of("java.util.Iterator")),
                _ -> Unit.INSTANCE,
                InvokeType.INTERFACE
        );

        instructionsSet.storeLocal(ClassDesc.of("java.util.Iterator"), iterableVariableValue.getSlot());

        var conditionLabel = instructionsSet.createAndInitLabel();
        var endLabel = instructionsSet.createAndInitLabel();
        LoopEnvironment loopEnvironment = Registries.LOOP_ENVIRONMENT_FACTORY.getEntry().getValue().create(environment, conditionLabel, endLabel);

        instructionsSet.bindLabel(conditionLabel);

        instructionsSet.getLocal(ClassDesc.of("java.util.Iterator"), iterableVariableValue.getSlot());
        instructionsSet.invokeMethod(
                ClassDesc.of("java.util.Iterator"),
                "hasNext",
                MethodTypeDesc.of(ConstantDescs.CD_boolean),
                _ -> Unit.INSTANCE,
                InvokeType.INTERFACE
        );
        instructionsSet.gotoLabelIfEqualsZero(endLabel);

        instructionsSet.getLocal(ClassDesc.of("java.util.Iterator"), iterableVariableValue.getSlot());
        instructionsSet.invokeMethod(
                ClassDesc.of("java.util.Iterator"),
                "next",
                MethodTypeDesc.of(ConstantDescs.CD_Object),
                _ -> Unit.INSTANCE,
                InvokeType.INTERFACE
        );

        dataType.resolve(environment);
        instructionsSet.checkCast(dataType.getClassDesc());

        VariableValue variableValue = localVariableDeclarationEnvironment.declareVariable(id, dataType, isConstant, null);
        instructionsSet.storeLocal(variableValue.getDataType().getClassDesc(), variableValue.getSlot());
        instructionsSet.setLocalName(variableValue.getSlot(), variableValue.getId(), variableValue.getDataType().getClassDesc(), conditionLabel, endLabel);

        for (Statement statement : body) {
            statement.emit(instructionsSet, loopEnvironment, this);
        }

        instructionsSet.gotoLabel(conditionLabel);
        instructionsSet.bindLabel(endLabel);
    }

    @Override
    public boolean alwaysReturns() {
        for (LocalStatement localStatement : body) {
            if (localStatement.alwaysReturns()) return true;
        }

        return false;
    }
}
