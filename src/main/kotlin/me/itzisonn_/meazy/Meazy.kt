package me.itzisonn_.meazy

import me.itzisonn_.meazy.Meazy.initialize
import me.itzisonn_.meazy.command.CommandExecutionResult
import me.itzisonn_.meazy.command.Commands
import me.itzisonn_.meazy.command.CommandResult
import me.itzisonn_.meazy.lexer.TokenTypeSets
import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.util.text.translatable
import me.itzisonn_.meazy.util.logger.LogLevel
import me.itzisonn_.meazy.util.logger.Logger
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.runtime.data.operator.Operators
import me.itzisonn_.meazy.util.version.Version
import kotlin.system.measureTimeMillis

object Meazy {
    val VERSION = Version.of("3.0")

    var isInitialized = false
        private set

    fun initialize() {
        check(!isInitialized) { "Meazy has already been initialized" }
        isInitialized = true

        Commands.initialize()
        TokenTypes.initialize()
        TokenTypeSets.initialize()
        Modifiers.initialize()
        Operators.initialize()
    }
}



fun main(args: Array<String>) {
    val initializationTime = measureTimeMillis {
        initialize()
    }

    Logger.log(
        LogLevel.INFO,
        translatable("commands.initialization_time", initializationTime / 1000.0)
    )

    when (val executionResult = Commands.execute(args.toList())) {
        CommandExecutionResult.NoArgs -> {
            Commands.logAvailableCommands()
            return
        }

        CommandExecutionResult.UnknownCommand -> {
            Logger.log(LogLevel.ERROR, translatable("commands.unknown", args[0]))
            Commands.logAvailableCommands()
            return
        }

        is CommandExecutionResult.Result -> {
            val result = executionResult.result

            val text = result.text ?: return
            val level = when (result) {
                is CommandResult.Success -> LogLevel.INFO
                is CommandResult.Failure -> LogLevel.ERROR
            }

            Logger.log(level, text)
        }
    }
}