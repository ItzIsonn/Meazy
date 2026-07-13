package me.itzisonn_.meazy.runtime.data.modifier

import me.itzisonn_.meazy.runtime.data.modifier.custom.AbstractModifier
import me.itzisonn_.meazy.runtime.data.modifier.custom.DataModifier
import me.itzisonn_.meazy.runtime.data.modifier.custom.EnumModifier
import me.itzisonn_.meazy.runtime.data.modifier.custom.GetModifier
import me.itzisonn_.meazy.runtime.data.modifier.custom.OpenModifier
import me.itzisonn_.meazy.runtime.data.modifier.custom.OperatorModifier
import me.itzisonn_.meazy.runtime.data.modifier.custom.OverrideModifier
import me.itzisonn_.meazy.runtime.data.modifier.custom.PrivateModifier
import me.itzisonn_.meazy.runtime.data.modifier.custom.ProtectedModifier
import me.itzisonn_.meazy.runtime.data.modifier.custom.SetModifier
import me.itzisonn_.meazy.runtime.data.modifier.custom.SharedModifier

/**
 * Modifiers registrar
 */
object Modifiers {
    private val modifiers = mutableSetOf<Modifier>()
    private var hasInitialized = false

    fun add(modifier: Modifier) {
        require(get(modifier.id) == null) { "Modifier with id '${modifier.id}' already exists" }
        modifiers += modifier
    }
    fun get(id: String) = modifiers.find { it.id == id }
    fun getAll() = modifiers.toSet()

    internal fun initialize() {
        check(!hasInitialized) { "Modifiers have already been initialized" }
        hasInitialized = true

        add(PrivateModifier())
        add(ProtectedModifier())
        add(OpenModifier())
        add(SharedModifier())
        add(OverrideModifier())
        add(AbstractModifier())
        add(GetModifier())
        add(SetModifier())
        add(DataModifier())
        add(OperatorModifier())
        add(EnumModifier())
    }



    val private get() = getNonNull("private")
    val protected get() = getNonNull("protected")
    val open get() = getNonNull("open")
    val shared get() = getNonNull("shared")
    val override get() = getNonNull("override")
    val abstract get() = getNonNull("abstract")
    val get get() = getNonNull("get")
    val set get() = getNonNull("set")
    val data get() = getNonNull("data")
    val operator get() = getNonNull("operator")
    val enum get() = getNonNull("enum")

    private fun getNonNull(id: String): Modifier {
        return get(id)!!
    }
}
