package me.itzisonn_.meazy.runtime.environment.factory;

import kotlin.uuid.Uuid;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Represents factory for creating {@link ConstructorEnvironment}s
 */
@NullMarked
public interface ConstructorEnvironmentFactory { //TODO javadoc
    /**
     * Creates constructor environment
     *
     * @param parent Parent
     * @return New constructor environment
     */
    ConstructorEnvironment create(ConstructorDeclarationEnvironment parent, @Nullable Uuid startLabel, @Nullable Uuid endLabel, Set<Modifier> modifiers, List<ParameterExpression> parameters);
}
