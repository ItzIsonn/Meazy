package me.itzisonn_.meazy.registry

import me.itzisonn_.meazy.parser.operator.Operator
import me.itzisonn_.registry.RegistryIdentifier
import me.itzisonn_.registry.multiple_entry.SetRegistry

class OperatorRegistry : SetRegistry<Operator>() {
    override fun register(identifier: RegistryIdentifier, value: Operator, overridable: Boolean) {
        for (entry in entries) {
            require(!(entry.getValue().id == value.id && !entry.isOverrideable)) {
                "Operator with id ${value.id} has already been registered"
            }
        }

        super.register(identifier, value, overridable)
    }
}
