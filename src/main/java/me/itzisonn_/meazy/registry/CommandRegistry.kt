package me.itzisonn_.meazy.registry

import me.itzisonn_.meazy.command.Command
import me.itzisonn_.registry.RegistryIdentifier
import me.itzisonn_.registry.multiple_entry.SetRegistry

class CommandRegistry : SetRegistry<Command>() {
    override fun register(identifier: RegistryIdentifier, value: Command, overridable: Boolean) {
        for (entry in entries) {
            require(!(entry.getValue().id == value.id && !entry.isOverrideable)) {
                "Command with id '${value.id}' has already been registered"
            }
        }

        super.register(identifier, value, overridable)
    }
}
