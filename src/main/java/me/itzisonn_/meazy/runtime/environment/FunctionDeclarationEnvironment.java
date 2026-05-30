package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.runtime.value.FunctionValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Adds to Environment ability to declare functions
 */
@NullMarked
public interface FunctionDeclarationEnvironment extends Environment {
    /**
     * Declares given function in this environment
     * TODO
     */
    FunctionValue declareFunction(String id, List<ParameterExpression> parameters, @Nullable DataType returnDataType, FunctionEnvironment functionEnvironment);

    /**
     * @param id Id
     * @param parameters Parameters
     * @return Declared function with given id and args or null
     */
    default Optional<FunctionValue> getFunction(String id, List<ClassDesc> parameters) {
        main:
        for (FunctionValue functionValue : getFunctions()) {
            if (!functionValue.getId().equals(id)) continue;

            List<ParameterExpression> functionParameters = functionValue.getParameters();
            if (functionParameters.size() != parameters.size()) continue;

            for (int i = 0; i < parameters.size(); i++) {
                ClassDesc functionParameterClassDesc = functionParameters.get(i).getDataType().getClassDesc();
                ClassDesc parameterClassDesc = parameters.get(i);
                if (!EnvironmentUtils.isInstanceOf(this, parameterClassDesc, functionParameterClassDesc)) continue main;
            }

            return Optional.of(functionValue);
        }

        return Optional.empty();
    }

    /**
     * @return All declared functions
     */
    Set<FunctionValue> getFunctions();
}