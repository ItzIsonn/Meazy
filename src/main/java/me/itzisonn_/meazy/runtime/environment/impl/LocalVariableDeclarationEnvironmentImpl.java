package me.itzisonn_.meazy.runtime.environment.impl;

import lombok.Getter;
import lombok.Setter;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LocalVariableDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.variable_value.VariableValue;
import me.itzisonn_.meazy.runtime.EvaluationException;
import me.itzisonn_.meazy.runtime.variable_value.VariableValueImpl;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NullMarked
public class LocalVariableDeclarationEnvironmentImpl extends VariableDeclarationEnvironmentImpl implements LocalVariableDeclarationEnvironment {
    @Nullable
    private UUID startLabel;
    @Nullable
    private UUID endLabel;

    public LocalVariableDeclarationEnvironmentImpl(Environment parent, @Nullable UUID startLabel, @Nullable UUID endLabel) {
        super(parent);
        this.startLabel = startLabel;
        this.endLabel = endLabel;
    }

    @Override
    public VariableValue declareVariable(@Nullable String id, DataType dataType, boolean isConstant, @Nullable Expression value) {
        if (id != null && getVariable(id).isPresent()) {
            throw new EvaluationException(Text.translatable("meazy:runtime.variable.already_exists", id));
        }

        int slot = getUsedSlotsCount();
        if (!isShared()) slot++;

        Environment parentEnvironment = parent;
        while (parentEnvironment instanceof LocalVariableDeclarationEnvironment localEnvironment) {
            slot += localEnvironment.getUsedSlotsCount();
            parentEnvironment = localEnvironment.getParent();
        }

        VariableValue variableValue = new VariableValueImpl(id, dataType, isConstant, Set.of(), slot, value, this);
        variables.add(variableValue);
        return variableValue;
    }

    @Override
    public int getUsedSlotsCount() {
        int usedSlots = 0;

        for (VariableValue variableValue : variables) {
            ClassDesc classDesc = variableValue.getDataType().getClassDesc();

            if (classDesc.equals(ConstantDescs.CD_double) || classDesc.equals(ConstantDescs.CD_long)) usedSlots += 2;
            else usedSlots += 1;
        }

        return usedSlots;
    }
}