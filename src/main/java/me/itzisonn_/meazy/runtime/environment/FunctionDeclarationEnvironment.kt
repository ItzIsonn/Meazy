package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import org.jspecify.annotations.NullMarked;

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
    void declareFunction(FunctionEnvironment functionEnvironment);

    /**
     * @param id Id
     * @param args Parameters
     * @return Declared function with given id and args or null
     */
    default Optional<FunctionEnvironment> getFunction(String id, List<DataType> args) {
        main:
        for (FunctionEnvironment functionEnvironment : getFunctions()) {
            if (!functionEnvironment.getId().equals(id)) continue;

            List<ParameterExpression> parameters = functionEnvironment.getParameters();
            if (parameters.size() != args.size()) continue;

            for (int i = 0; i < args.size(); i++) {
                DataType parameter = parameters.get(i).getDataType();
                DataType arg = args.get(i);
                if (!DataType.matches(this, arg, parameter)) continue main;
            }

            return Optional.of(functionEnvironment);
        }

        return Optional.empty();
    }

    /**
     * @return All declared functions
     */
    Set<FunctionEnvironment> getFunctions();
}