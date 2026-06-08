package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import org.jspecify.annotations.NullMarked;

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
     * @param args Constructor's args TODO
     * @return Declared constructor with given args or null
     */
    default Optional<ConstructorEnvironment> getConstructor(List<DataType> args) {
        main:
        for (ConstructorEnvironment constructorEnvironment : getConstructors()) {
            List<ParameterExpression> parameters = constructorEnvironment.getParameters();
            if (args.size() != parameters.size()) continue;

            for (int i = 0; i < args.size(); i++) {
                DataType parameter = parameters.get(i).getDataType();
                DataType arg = args.get(i);
                if (!DataType.matches(this, arg, parameter)) continue main;
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