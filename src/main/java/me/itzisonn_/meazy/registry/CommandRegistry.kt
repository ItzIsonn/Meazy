package me.itzisonn_.meazy.registry

import me.itzisonn_.meazy.command.AbstractCommand
import me.itzisonn_.registry.RegistryIdentifier
import me.itzisonn_.registry.multiple_entry.SetRegistry

class CommandRegistry : SetRegistry<AbstractCommand>() {
    override fun register(identifier: RegistryIdentifier, value: AbstractCommand, overridable: Boolean) {
        for (entry in entries) {
            require(!(entry.getValue().name == value.name && !entry.isOverrideable)) { "AbstractCommand with name " + value.name + " has already been registered" }
        }

        super.register(identifier, value, overridable)
    }
}
