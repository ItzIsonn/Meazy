package me.itzisonn_.meazy.parser.modifier

import me.itzisonn_.meazy.parser.modifier.custom.*

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
