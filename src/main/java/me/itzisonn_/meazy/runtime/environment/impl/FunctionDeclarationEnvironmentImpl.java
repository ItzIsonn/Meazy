package me.itzisonn_.meazy.runtime.environment.impl;

import lombok.Getter;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.value.FunctionValue;
import me.itzisonn_.meazy.runtime.EvaluationException;
import me.itzisonn_.meazy.runtime.value.impl.FunctionValueImpl;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NullMarked
public abstract class FunctionDeclarationEnvironmentImpl extends EnvironmentImpl implements FunctionDeclarationEnvironment {
    protected final Set<FunctionValue> functions;
    @Getter
    protected final boolean isShared;

    public FunctionDeclarationEnvironmentImpl(Environment parent, boolean isShared) {
        super(parent);
        functions = new HashSet<>();
        this.isShared = isShared;
    }

    @Override
    public FunctionValue declareFunction(String id, List<ParameterExpression> parameters, @Nullable DataType returnDataType, FunctionEnvironment functionEnvironment) {
        main:
        for (FunctionValue functionValue : functions) {
            if (functionValue.getId().equals(id)) {
                List<ParameterExpression> otherParameters = functionValue.getParameters();
                if (parameters.size() != otherParameters.size()) continue;

                for (int i = 0; i < parameters.size(); i++) {
                    if (!otherParameters.get(i).getDataType().equals(parameters.get(i).getDataType())) continue main;
                }

                throw new EvaluationException(Text.translatable("meazy:runtime.function.already_exists", id));
            }
        }

        FunctionValue functionValue = new FunctionValueImpl(id, parameters, returnDataType, functionEnvironment, Set.of());
        functions.add(functionValue);
        return functionValue;
    }

    @Override
    public Set<FunctionValue> getFunctions() {
        return new HashSet<>(functions);
    }
}