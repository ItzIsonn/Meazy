package me.itzisonn_.meazy.runtime.data.modifier

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

        add(Modifier("private"))
        add(Modifier("protected"))
        add(Modifier("open"))
        add(Modifier("shared"))
        add(Modifier("override"))
        add(Modifier("abstract"))
        add(Modifier("operator"))
    }



    val private get() = getNonNull("private")
    val protected get() = getNonNull("protected")
    val open get() = getNonNull("open")
    val shared get() = getNonNull("shared")
    val override get() = getNonNull("override")
    val abstract get() = getNonNull("abstract")
    val operator get() = getNonNull("operator")

    private fun getNonNull(id: String) = get(id)!!
}
