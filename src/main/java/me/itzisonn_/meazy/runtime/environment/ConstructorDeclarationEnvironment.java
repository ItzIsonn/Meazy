package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.runtime.value.ConstructorValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.util.List;
import java.util.Set;

/**
 * Adds to Environment ability to declare constructors
 */
@NullMarked
public interface ConstructorDeclarationEnvironment extends Environment {
    /**
     * Declares given constructor in this environment
     * @param value Constructor value
     */
    void declareConstructor(ConstructorValue value);

    /**
     * @param parameters Constructor's args TODO
     * @return Declared constructor with given args or null
     */
    @Nullable
    default ConstructorValue getConstructor(List<ClassDesc> parameters) {
        main:
        for (ConstructorValue constructorValue : getConstructors()) {
            List<ParameterExpression> constructorParameters = constructorValue.getParameters();
            if (parameters.size() != constructorParameters.size()) continue;

            for (int i = 0; i < parameters.size(); i++) {
                if (!constructorParameters.get(i).getDataType().getClassDescriptor(this).equals(parameters.get(i))) continue main;
            }

            return constructorValue;
        }

        return null;
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
    Set<ConstructorValue> getConstructors();
}