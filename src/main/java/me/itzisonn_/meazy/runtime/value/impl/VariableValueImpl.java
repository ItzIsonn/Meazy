package me.itzisonn_.meazy.runtime.value.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.VariableDeclarationEnvironment;
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.registry.RegistryEntry;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link VariableValue}
 */
@Getter
@EqualsAndHashCode(callSuper = false, doNotUseGetters = true)
@NullMarked
public class VariableValueImpl implements VariableValue {
    @Nullable
    private final String id;
    private final DataType dataType;
    private final boolean isConstant;
    private final int slot;
    @Nullable
    private final Expression initializer;
    private final VariableDeclarationEnvironment parentEnvironment;
    private final Set<Modifier> modifiers;

    /**
     * @param id Id
     * @param dataType DataType
     * @param isConstant Whether value is constant
     * @param modifiers Modifiers
     * @param parentEnvironment Parent environment
     */
    public VariableValueImpl(@Nullable String id, DataType dataType, boolean isConstant, Set<Modifier> modifiers, int slot, @Nullable Expression initializer, VariableDeclarationEnvironment parentEnvironment) {
        this.id = id;
        this.dataType = dataType;
        this.isConstant = isConstant;
        this.slot = slot;
        this.initializer = initializer;
        this.parentEnvironment = parentEnvironment;
        this.modifiers = modifiers;
    }


    @Override
    public boolean isAccessible(Environment environment) {
        if (id == null) return false;
        Identifier identifier = new VariableIdentifier(id);

        for (RegistryEntry<Modifier> entry : Registries.MODIFIERS.getEntries()) {
            Modifier modifier = entry.getValue();
            if (!modifier.canAccess(environment, getParentEnvironment(), identifier, getModifiers().contains(modifier))) return false;
        }

        return true;
    }

    @Override
    public Set<Modifier> getModifiers() {
        return new HashSet<>(modifiers);
    }
}