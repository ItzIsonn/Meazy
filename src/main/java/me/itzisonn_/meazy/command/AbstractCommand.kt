package me.itzisonn_.meazy.command

import me.itzisonn_.meazy.MeazyMain
import me.itzisonn_.meazy.lang.text.Text
import me.itzisonn_.meazy.registry.Registries

/**
 * Represents command
 * 
 * @see Registries.COMMANDS
 *
 * @param name Name
 * @param args List of args names
 * @throws IllegalArgumentException If either name or any of args doesn't match [MeazyMain.IDENTIFIER_REGEX]
 */
abstract class AbstractCommand(name: String, args: List<String>) {
    /**
     * Name
     */
    val name: String

    /**
     * List of args names
     */
    private val _args: List<String>
    val args get() = _args.toList()

    init {
        require(name.matches(MeazyMain.IDENTIFIER_REGEX.toRegex())) { "Invalid command's name" }
        require(args.all { it.matches(MeazyMain.IDENTIFIER_REGEX.toRegex()) }) { "Invalid arg's name" }

        this.name = name
        this._args = args
    }

    /**
     * Executes this command with given args.
     * Args' amount matches [AbstractCommand._args]' size
     * 
     * @param args Args
     * @return Success message that will be logged or null
     */
    abstract fun execute(vararg args: String): Text?
}
