package me.itzisonn_.meazy

import me.itzisonn_.meazy.command.Argument
import me.itzisonn_.meazy.command.Commands
import me.itzisonn_.meazy.command.CommandResult
import me.itzisonn_.meazy.command.LiteralArgument
import me.itzisonn_.meazy.command.TypedArgument
import me.itzisonn_.meazy.datagen.DatagenManager
import me.itzisonn_.meazy.datagen.deserializer.TokenTypeDeserializer
import me.itzisonn_.meazy.datagen.deserializer.TokenTypeSetDeserializer
import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.meazy.registry.defaultIdentifier
import me.itzisonn_.meazy.settings.SettingsManager.settings
import me.itzisonn_.meazy.text.TranslationsBundle
import me.itzisonn_.meazy.text.literal
import me.itzisonn_.meazy.text.translatable
import me.itzisonn_.meazy.logger.LogLevel
import me.itzisonn_.meazy.logger.Logger
import me.itzisonn_.meazy.version.Version

object MeazyMain {
    val VERSION = Version.of("3.0")

    /**
     * Regex used by all identifiers
     */
    const val IDENTIFIER_REGEX = "[a-zA-Z_][a-zA-Z0-9_]*"

    var isInitialized = false
        private set

    @JvmStatic
    fun main(vararg args: String) {
        val startLoadMillis = System.currentTimeMillis()
        initialize()
        val endLoadMillis = System.currentTimeMillis()

        Logger.log(
            LogLevel.INFO,
            translatable(
                "meazy:commands.loaded_info",
                (endLoadMillis - startLoadMillis) / 1000.toDouble()
            )
        )

        val args = args.toList()
        if (args.isEmpty()) {
            showAvailableCommandsList()
            return
        }

        val command = Commands.findById(args[0])
        if (command == null) {
            Logger.log(LogLevel.ERROR, translatable("meazy:commands.unknown", args[0]))
            showAvailableCommandsList()
            return
        }

        val commandArgs = args.subList(1, args.size)
        val result = command.execute(commandArgs)

        val text = result.text ?: return
        val level = when (result) {
            is CommandResult.Success -> LogLevel.INFO
            is CommandResult.Failure -> LogLevel.ERROR
        }

        Logger.log(level, text)
    }

    private fun showAvailableCommandsList() {
        Logger.log(LogLevel.INFO, translatable("meazy:commands.available"))

        for (entry in Registries.COMMANDS.entries) {
            val command = entry.getValue()

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


    fun initialize() {
        check(!isInitialized) { "MeazyMain have already been initialized" }
        isInitialized = true

        Registries.initialize()

        val stringLanguage = settings.language
        val languagesEntry = Registries.LANGUAGES.getEntry(stringLanguage)
        if (languagesEntry == null) Logger.log(LogLevel.ERROR, translatable("meazy:settings.unknown_language", stringLanguage))
        else TranslationsBundle.setLanguage(languagesEntry.getValue())

        for (tokenType in DatagenManager.getDeserializedMultiple("token_type", TokenTypeDeserializer)) {
            val id = defaultIdentifier(tokenType.id)
            if (Registries.TOKEN_TYPES.getEntry(id) != null) continue
            Registries.TOKEN_TYPES.register(defaultIdentifier(tokenType.id), tokenType)
        }

        for (tokenTypeSet in DatagenManager.getDeserializedSingle("token_type_set", TokenTypeSetDeserializer)) {
            Registries.TOKEN_TYPE_SETS.register(defaultIdentifier(tokenTypeSet.id), tokenTypeSet)
        }
    }
}