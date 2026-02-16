package me.itzisonn_.meazy.runtime.value;

import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Represents constructor value
 */
@NullMarked
public interface ConstructorValue extends ModifierableValue {
    /**
     * @return Parameters
     */
    List<ParameterExpression> getParameters();

    /**
     * @return Constructor environment
     */
    ConstructorEnvironment getEnvironment();
}
