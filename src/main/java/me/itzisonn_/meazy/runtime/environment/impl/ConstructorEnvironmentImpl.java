package me.itzisonn_.meazy.runtime.environment.impl;

import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NullMarked
public class ConstructorEnvironmentImpl extends LocalVariableDeclarationEnvironmentImpl implements ConstructorEnvironment {
    protected final Set<Modifier> modifiers;

    public ConstructorEnvironmentImpl(ConstructorDeclarationEnvironment parent, @Nullable UUID startLabel, @Nullable UUID endLabel, Set<Modifier> modifiers) {
        super(parent, startLabel, endLabel);
        this.modifiers = modifiers;
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
    public Set<Modifier> getModifiers() {
        return new HashSet<>(modifiers);
    }
}