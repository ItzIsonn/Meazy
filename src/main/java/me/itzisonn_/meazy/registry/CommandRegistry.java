package me.itzisonn_.meazy.registry;

import me.itzisonn_.meazy.command.Command;
import me.itzisonn_.registry.RegistryEntry;
import me.itzisonn_.registry.RegistryIdentifier;
import me.itzisonn_.registry.multiple_entry.SetRegistry;

public class CommandRegistry extends SetRegistry<Command> {
    @Override
    public void register(RegistryIdentifier identifier, Command value, boolean overridable) {
        for (RegistryEntry<Command> entry : getEntries()) {
            if (entry.getValue().getName().equals(value.getName()) && !entry.isOverrideable()) {
                throw new IllegalArgumentException("Command with name " + value.getName() + " has already been registered");
            }
        }

        super.register(identifier, value, overridable);
    }
}
