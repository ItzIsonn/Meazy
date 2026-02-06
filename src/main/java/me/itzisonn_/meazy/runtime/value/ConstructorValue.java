package me.itzisonn_.meazy.runtime.value;

import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Represents constructor value
 */
@NullMarked
public interface ConstructorValue extends ModifierableRuntimeValue {
    /**
     * @return Parameters
     */
    List<ParameterExpression> getParameters();

    /**
     * @return Parent environment
     */
    ConstructorDeclarationEnvironment getParentEnvironment();
}
