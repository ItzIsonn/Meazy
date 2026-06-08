package me.itzisonn_.meazy.command

import me.itzisonn_.meazy.MeazyMain
import me.itzisonn_.meazy.command.custom.*
import me.itzisonn_.meazy.registry.Registries

/**
 * Commands registrar
 * 
 * @see Registries.COMMANDS
 */
object Commands {
    private var hasRegistered = false

    /**
     * Finds registered AbstractCommand with given name
     * 
     * @param name AbstractCommand's name
     * @return AbstractCommand with given name or null
     */
    fun getByName(name: String): AbstractCommand? {
        for (entry in Registries.COMMANDS.getEntries()) {
            if (entry.getValue().name == name) return entry.getValue()
        }

        return null
    }


    /**
     * Initializes [Registries.COMMANDS] registry
     * 
     * 
     * *Don't use this method because it's called once at [Registries] initialization*
     * 
     * @throws IllegalStateException If [Registries.COMMANDS] registry has already been initialized
     */
    fun register() {
        check(!hasRegistered) { "Commands have already been initialized" }
        hasRegistered = true

        register(VersionCommand())
        register(AddonsCommand())
        register(RunCommand())
        register(CompileCommand())
        register(CompileAndRunCommand())
    }


    private fun register(command: AbstractCommand) {
        Registries.COMMANDS.register(MeazyMain.getDefaultIdentifier(command.name), command)
    }
}
