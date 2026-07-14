package me.itzisonn_.meazy.command

import me.itzisonn_.meazy.command.custom.*
import me.itzisonn_.meazy.util.logger.LogLevel
import me.itzisonn_.meazy.util.logger.Logger
import me.itzisonn_.meazy.util.text.literal
import me.itzisonn_.meazy.util.text.translatable

/**
 * Commands registrar
 */
object Commands {
    private val commands = mutableSetOf<Command>()
    private var hasInitialized = false

    fun add(command: Command) {
        require(get(command.id) == null) { "Command with id '${command.id}' already exists" }
        commands += command
    }
    fun get(id: String) = commands.find { it.id == id }
    fun getAll() = commands.toSet()

    internal fun initialize() {
        check(!hasInitialized) { "Commands have already been initialized" }
        hasInitialized = true

        add(versionCommand)
        add(runCommand)
        add(compileCommand)
        add(compileAndRunCommand)
    }

    fun execute(args: List<String>): CommandExecutionResult {
        if (args.isEmpty()) return CommandExecutionResult.NoArgs
        val command = get(args[0]) ?: return CommandExecutionResult.UnknownCommand

        val commandArgs = args.subList(1, args.size)
        val result = command.execute(commandArgs)

        return CommandExecutionResult.Result(result)
    }

    fun logAvailableCommands() {
        Logger.log(LogLevel.INFO, translatable("commands.available"))

        for (command in getAll()) {
            var argsString = ""
            if (command.arguments.size >= 2) argsString += "["
            argsString += command.arguments.joinToString(" | ") { getStringRepresentation(it) }
            if (command.arguments.size >= 2) argsString += "]"

            Logger.log(
                LogLevel.INFO,
                literal("  {0} {1}", command.id, argsString)
            )
        }
    }

    private fun getStringRepresentation(argument: Argument): String {
        var string = when (argument) {
            is LiteralArgument -> argument.id
            is TypedArgument<*> -> "<" + argument.id + ">"
        }

        if (argument.children.isEmpty()) return string
        string += " "

        if (argument.children.size >= 2) string += "["
        string += argument.children.joinToString(" | ") { getStringRepresentation(it) }
        if (argument.children.size >= 2) string += "]"

        return string
    }
}
