package me.itzisonn_.meazy.runtime.environment.factory.impl;

import kotlin.uuid.Uuid;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment;
import me.itzisonn_.meazy.runtime.environment.impl.ConstructorEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.factory.ConstructorEnvironmentFactory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@NullMarked
public class ConstructorEnvironmentFactoryImpl implements ConstructorEnvironmentFactory {
    @Override
    public ConstructorEnvironment create(ConstructorDeclarationEnvironment parent, @Nullable Uuid startLabel, @Nullable Uuid endLabel, Set<Modifier> modifiers, List<ParameterExpression> parameters) {
        return new ConstructorEnvironmentImpl(parent, startLabel, endLabel, modifiers, parameters);
    }
}
