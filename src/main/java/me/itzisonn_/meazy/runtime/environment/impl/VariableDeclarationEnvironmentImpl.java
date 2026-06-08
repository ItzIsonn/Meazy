package me.itzisonn_.meazy.runtime.environment.impl;

import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.VariableDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.variable_value.VariableValue;
import me.itzisonn_.meazy.runtime.EvaluationException;
import me.itzisonn_.meazy.runtime.variable_value.VariableValueImpl;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NullMarked
public class VariableDeclarationEnvironmentImpl extends EnvironmentImpl implements VariableDeclarationEnvironment {
    protected final List<VariableValue> variables;

    public VariableDeclarationEnvironmentImpl(Environment parent) {
        super(parent);
        variables = new ArrayList<>();
    }

    @Override
    public VariableValue declareVariable(String id, DataType dataType, boolean isConstant, @Nullable Expression value) {
        if (getVariable(id).isPresent()) {
            throw new EvaluationException(Text.translatable("meazy:runtime.variable.already_exists", id));
        }

        VariableValue variableValue = new VariableValueImpl(id, dataType, isConstant, Set.of(), -1, value, this);
        variables.add(variableValue);
        return variableValue;
    }

    @Override
    public List<VariableValue> getVariables() {
        return new ArrayList<>(variables);
    }

    @Override
    public boolean isShared() {
        return parent.isShared();
    }
}