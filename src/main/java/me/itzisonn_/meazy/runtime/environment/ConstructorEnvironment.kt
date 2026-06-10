package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Represents environment for constructors
 */
@NullMarked
public interface ConstructorEnvironment extends LocalVariableDeclarationEnvironment, ModifieredEnvironment {
    @Override
    ConstructorDeclarationEnvironment getParent();

    List<ParameterExpression> getParameters();
}