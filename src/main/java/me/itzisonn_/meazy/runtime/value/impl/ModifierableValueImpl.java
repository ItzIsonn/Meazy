package me.itzisonn_.meazy.runtime.value.impl;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.runtime.value.ModifierableValue;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

/**
 * Implementation of {@link ModifierableValue}
 */
@Getter
@EqualsAndHashCode
@NullMarked
public abstract class ModifierableValueImpl implements ModifierableValue {
    protected final Set<Modifier> modifiers;

    public ModifierableValueImpl(Set<Modifier> modifiers) {
        this.modifiers = Set.copyOf(modifiers);
    }
}