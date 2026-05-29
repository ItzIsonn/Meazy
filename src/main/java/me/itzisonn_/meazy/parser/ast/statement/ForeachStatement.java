package me.itzisonn_.meazy.parser.ast.statement;

import lombok.Getter;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.instruction.InstructionsSet;
import me.itzisonn_.meazy.instruction.method.InvokeMethodInstruction.InvokeType;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.EnvironmentUtils;
import me.itzisonn_.meazy.runtime.environment.LocalVariableDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.util.List;
import java.util.UUID;

@Getter
@NullMarked
public class ForeachStatement implements Statement {
    private final VariableDeclarationStatement variableDeclarationStatement;
    private final Expression collection;
    private final List<Statement> body;

    public ForeachStatement(VariableDeclarationStatement variableDeclarationStatement, Expression collection, List<Statement> body) {
        this.variableDeclarationStatement = variableDeclarationStatement;
        this.collection = collection;
        this.body = body;
    }

    @Override
    public void emit(InstructionsSet instructionsSet, Environment environment, Statement parent) {
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
                _ -> {},
                InvokeType.INTERFACE
        );

        instructionsSet.storeLocal(ClassDesc.of("java.util.Iterator"), iterableVariableValue.getSlot());

        UUID conditionLabel = instructionsSet.createAndInitLabel();
        UUID endLabel = instructionsSet.createAndInitLabel();
        LoopEnvironment loopEnvironment = Registries.LOOP_ENVIRONMENT_FACTORY.getEntry().getValue().create(environment, conditionLabel, endLabel);

        instructionsSet.bindLabel(conditionLabel);

        instructionsSet.getLocal(ClassDesc.of("java.util.Iterator"), iterableVariableValue.getSlot());
        instructionsSet.invokeMethod(
                ClassDesc.of("java.util.Iterator"),
                "hasNext",
                MethodTypeDesc.of(ConstantDescs.CD_boolean),
                _ -> {},
                InvokeType.INTERFACE
        );
        instructionsSet.gotoLabelIfEqualsZero(endLabel);

        instructionsSet.getLocal(ClassDesc.of("java.util.Iterator"), iterableVariableValue.getSlot());
        instructionsSet.invokeMethod(
                ClassDesc.of("java.util.Iterator"),
                "next",
                MethodTypeDesc.of(ConstantDescs.CD_Object),
                _ -> {},
                InvokeType.INTERFACE
        );

        ClassDesc classDesc = EnvironmentUtils.resolveClassDesc(environment, variableDeclarationStatement.getDataType().getClassDesc());
        instructionsSet.checkCast(classDesc);

        VariableValue variableValue = localVariableDeclarationEnvironment.declareVariable(
                variableDeclarationStatement.getId(),
                DataType.of(classDesc, variableDeclarationStatement.getDataType().isNullable()),
                variableDeclarationStatement.isConstant(),
                null
        );

        instructionsSet.storeLocal(classDesc, variableValue.getSlot());
        instructionsSet.setLocalName(variableValue.getSlot(), variableValue.getId(), variableValue.getDataType().getClassDesc(), conditionLabel, endLabel);

        for (Statement statement : body) {
            statement.emit(instructionsSet, loopEnvironment, this);
        }

        instructionsSet.gotoLabel(conditionLabel);
        instructionsSet.bindLabel(endLabel);
    }
}
