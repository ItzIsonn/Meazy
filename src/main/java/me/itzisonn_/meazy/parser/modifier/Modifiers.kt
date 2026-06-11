package me.itzisonn_.meazy.parser.modifier

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.parser.modifier.custom.*
import me.itzisonn_.meazy.registry.Registries

/**
 * Modifiers registrar
 * 
 * @see Registries.MODIFIERS
 */
object Modifiers {
    private var hasRegistered = false

    val private get() = get("private")
    val protected get() = get("protected")
    val open get() = get("open")
    val shared get() = get("shared")
    val override get() = get("override")
    val abstract get() = get("abstract")
    val get get() = get("get")
    val set get() = get("set")
    val data get() = get("data")
    val operator get() = get("operator")
    val enum get() = get("enum")

    /**
     * Finds registered Modifier with given id
     * 
     * @param id Id of Modifier
     * @return Modifier with given id or null
     */
    fun parse(id: String): Modifier? {
        for (entry in Registries.MODIFIERS.getEntries()) {
            if (id == entry.getValue().id) return entry.getValue()
        }

        return null
    }

    

    /**
     * Initializes [Registries.MODIFIERS] registry
     * 
     * 
     * *Don't use this method because it's called once at [Registries] initialization*
     * 
     * @throws IllegalStateException If [Registries.MODIFIERS] registry has already been initialized
     */
    fun register() {
        check(!hasRegistered) { "Modifiers have already been initialized" }
        hasRegistered = true

        register(PrivateModifier())
        register(ProtectedModifier())
        register(OpenModifier())
        register(SharedModifier())
        register(OverrideModifier())
        register(AbstractModifier())
        register(GetModifier())
        register(SetModifier())
        register(DataModifier())
        register(OperatorModifier())
        register(EnumModifier())
    }

    private fun register(modifier: Modifier) {
        Registries.MODIFIERS.register(getDefaultIdentifier(modifier.id), modifier)
    }

    private fun get(id: String): Modifier {
        return Registries.MODIFIERS.getEntry(getDefaultIdentifier(id)).value!!
    }
}
