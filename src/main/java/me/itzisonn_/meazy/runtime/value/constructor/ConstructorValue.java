package me.itzisonn_.meazy.runtime.value.constructor;

import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;

import java.util.List;
import java.util.Set;

/**
 * Represents constructor value
 */
public interface ConstructorValue extends RuntimeValue<Object> {
    /**
     * @return Args
     */
    List<CallArgExpression> getArgs();

    /**
     * @return Parent environment
     */
    ConstructorDeclarationEnvironment getParentEnvironment();

    /**
     * @return Modifiers
     */
    Set<Modifier> getModifiers();
}
