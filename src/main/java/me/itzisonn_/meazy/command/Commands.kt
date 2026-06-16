package me.itzisonn_.meazy.command

import me.itzisonn_.meazy.command.custom.*
import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.meazy.registry.defaultIdentifier

/**
 * Commands registrar
 * 
 * @see Registries.COMMANDS
 */
object Commands {
    private var hasRegistered = false

    /**
     * Finds registered Command with given name
     * 
     * @param id Command's id
     * @return Command with given id or null
     */
    fun findById(id: String): Command? {
        return Registries.COMMANDS.entries.map { it.value }.find { it.id == id }
    }



    /**
     * Initializes [Registries.COMMANDS] registry
     *
     * *Don't use this method because it's called once at [Registries] initialization*
     * 
     * @throws IllegalStateException If [Registries.COMMANDS] registry has already been initialized
     */
    fun register() {
        check(!hasRegistered) { "Commands have already been initialized" }
        hasRegistered = true

        register(versionCommand)
        register(runCommand)
        register(compileCommand)
        register(compileAndRunCommand)
    }

    private fun register(command: Command) {
        Registries.COMMANDS.register(defaultIdentifier(command.id), command)
    }
}
