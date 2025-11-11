package me.itzisonn_.meazy.registry;

import me.itzisonn_.meazy.command.AbstractCommand;
import me.itzisonn_.registry.RegistryEntry;
import me.itzisonn_.registry.RegistryIdentifier;
import me.itzisonn_.registry.multiple_entry.SetRegistry;

public class CommandRegistry extends SetRegistry<AbstractCommand> {
    @Override
    public void register(RegistryIdentifier identifier, AbstractCommand value, boolean overridable) {
        for (RegistryEntry<AbstractCommand> entry : getEntries()) {
            if (entry.getValue().getName().equals(value.getName()) && !entry.isOverrideable()) {
                throw new IllegalArgumentException("AbstractCommand with name " + value.getName() + " has already been registered");
            }
        }

        super.register(identifier, value, overridable);
    }
}
