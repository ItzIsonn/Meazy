package me.itzisonn_.meazy.command

import me.itzisonn_.meazy.command.custom.*

/**
 * Commands registrar
 */
object Commands {
    private val commands = mutableSetOf<Command>()
    private var hasInitialized = false

    fun add(command: Command) { commands += command }
    fun get(id: String) = commands.find { it.id == id }
    fun getAll() = commands.toSet()

    fun initialize() {
        check(!hasInitialized) { "Commands have already been initialized" }
        hasInitialized = true

        add(versionCommand)
        add(runCommand)
        add(compileCommand)
        add(compileAndRunCommand)
    }
}
