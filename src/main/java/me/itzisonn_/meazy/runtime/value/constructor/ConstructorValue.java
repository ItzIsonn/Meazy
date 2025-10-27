package me.itzisonn_.meazy.runtime.value.constructor;

import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.value.ModifierableRuntimeValue;

import java.util.List;

/**
 * Represents constructor value
 */
public interface ConstructorValue extends ModifierableRuntimeValue<Object> {
    /**
     * @return Args
     */
    List<CallArgExpression> getArgs();

    /**
     * @return Parent environment
     */
    ConstructorDeclarationEnvironment getParentEnvironment();
}
