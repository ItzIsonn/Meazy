package me.itzisonn_.meazy.runtime.environment.impl;

import kotlin.uuid.Uuid;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public class ConstructorEnvironmentImpl extends LocalVariableDeclarationEnvironmentImpl implements ConstructorEnvironment {
    protected final Set<Modifier> modifiers;
    protected final List<ParameterExpression> parameters;

    public ConstructorEnvironmentImpl(ConstructorDeclarationEnvironment parent, @Nullable Uuid startLabel, @Nullable Uuid endLabel, Set<Modifier> modifiers, List<ParameterExpression> parameters) {
        super(parent, startLabel, endLabel);
        this.modifiers = modifiers;
        this.parameters = parameters;
    }

    @Override
    public boolean isShared() {
        return false;
    }

    @Override
    public ConstructorDeclarationEnvironment getParent() {
        return (ConstructorDeclarationEnvironment) parent;
    }

    @Override
    public List<ParameterExpression> getParameters() {
        return new ArrayList<>(parameters);
    }

    @Override
    public Set<Modifier> getModifiers() {
        return new HashSet<>(modifiers);
    }
}