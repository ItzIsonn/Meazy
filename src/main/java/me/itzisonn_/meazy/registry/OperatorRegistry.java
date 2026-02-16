package me.itzisonn_.meazy.registry;

import me.itzisonn_.meazy.parser.operator.Operator;
import me.itzisonn_.registry.RegistryEntry;
import me.itzisonn_.registry.RegistryIdentifier;
import me.itzisonn_.registry.multiple_entry.SetRegistry;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class OperatorRegistry extends SetRegistry<Operator> {
    @Override
    public void register(RegistryIdentifier identifier, Operator value, boolean overridable) {
        for (RegistryEntry<Operator> entry : getEntries()) {
            if (entry.getValue().getId().equals(value.getId()) && !entry.isOverrideable()) {
                throw new IllegalArgumentException("Operator with id " + value.getId() + " has already been registered");
            }
        }

        super.register(identifier, value, overridable);
    }
}
