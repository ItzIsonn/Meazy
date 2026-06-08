package me.itzisonn_.meazy.runtime.environment.impl;

import lombok.Getter;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.EvaluationException;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NullMarked
public abstract class FunctionDeclarationEnvironmentImpl extends EnvironmentImpl implements FunctionDeclarationEnvironment {
    protected final Set<FunctionEnvironment> functions;
    @Getter
    protected final boolean isShared;

    public FunctionDeclarationEnvironmentImpl(Environment parent, boolean isShared) {
        super(parent);
        functions = new HashSet<>();
        this.isShared = isShared;
    }

    @Override
    public void declareFunction(FunctionEnvironment functionEnvironment) {
        List<ParameterExpression> parameters = functionEnvironment.getParameters();

        main:
        for (FunctionEnvironment otherFunctionEnvironment : functions) {
            if (otherFunctionEnvironment.getId().equals(functionEnvironment.getId())) {
                List<ParameterExpression> otherParameters = otherFunctionEnvironment.getParameters();
                if (parameters.size() != otherParameters.size()) continue;

                for (int i = 0; i < parameters.size(); i++) {
                    if (!otherParameters.get(i).getDataType().equals(parameters.get(i).getDataType())) continue main;
                }

                throw new EvaluationException(Text.translatable("meazy:runtime.function.already_exists", functionEnvironment.getId()));
            }
        }

        functions.add(functionEnvironment);
    }

    @Override
    public Set<FunctionEnvironment> getFunctions() {
        return new HashSet<>(functions);
    }
}