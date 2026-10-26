package me.itzisonn_.meazy.runtime.data.modifier

/**
 * Defines behavior of statements
 * @param id Id
 */
class Modifier(val id: String) {
    override fun toString() = "Modifier($id)"

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Modifier) return false
        return id == other.id
    }

    override fun hashCode() = id.hashCode()
}