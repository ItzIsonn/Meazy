package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ClassDesc;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Adds to Environment ability to declare constructors
 */
@NullMarked
public interface ConstructorDeclarationEnvironment extends Environment {
    /**
     * Declares given constructor in this environment
     * TODO
     */
    void declareConstructor(ConstructorEnvironment constructorEnvironment);

    /**
     * @param parameters Constructor's args TODO
     * @return Declared constructor with given args or null
     */
    default Optional<ConstructorEnvironment> getConstructor(List<ClassDesc> parameters) {
        main:
        for (ConstructorEnvironment constructorEnvironment : getConstructors()) {
            List<ParameterExpression> constructorParameters = constructorEnvironment.getParameters();
            if (parameters.size() != constructorParameters.size()) continue;

            for (int i = 0; i < parameters.size(); i++) {
                ClassDesc constructorParameterClassDesc = constructorParameters.get(i).getDataType().getClassDesc();
                ClassDesc parameterClassDesc = parameters.get(i);
                if (!EnvironmentUtils.isInstanceOf(this, parameterClassDesc, constructorParameterClassDesc)) continue main;
            }

            return Optional.of(constructorEnvironment);
        }

        return Optional.empty();
    }

    /**
     * @return Whether this environment has at least one declared constructor
     */
    default boolean hasConstructor() {
        return !getConstructors().isEmpty();
    }

    /**
     * @return All declared constructors
     */
    Set<ConstructorEnvironment> getConstructors();
}