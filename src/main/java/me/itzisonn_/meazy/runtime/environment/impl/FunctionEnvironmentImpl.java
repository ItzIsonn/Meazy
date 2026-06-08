package me.itzisonn_.meazy.runtime.environment.impl;

import lombok.Getter;
import lombok.Setter;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@NullMarked
public class FunctionEnvironmentImpl extends LocalVariableDeclarationEnvironmentImpl implements FunctionEnvironment {
    private final String id;
    private final List<ParameterExpression> parameters;
    @Setter
    @Nullable
    private DataType returnDataType;
    private final boolean isShared;
    protected final Set<Modifier> modifiers;

    public FunctionEnvironmentImpl(FunctionDeclarationEnvironment parent, @Nullable UUID startLabel, @Nullable UUID endLabel, String id, List<ParameterExpression> parameters, @Nullable DataType returnDataType, boolean isShared, Set<Modifier> modifiers) {
        super(parent, startLabel, endLabel);
        this.id = id;
        this.parameters = parameters;
        this.returnDataType = returnDataType;
        this.isShared = isShared;
        this.modifiers = modifiers;
    }

    @Override
    public FunctionDeclarationEnvironment getParent() {
        return (FunctionDeclarationEnvironment) parent;
    }

    @Override
    public Set<Modifier> getModifiers() {
        return new HashSet<>(modifiers);
    }
}