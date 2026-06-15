package me.itzisonn_.meazy

import me.itzisonn_.meazy.command.Commands.getByName
import me.itzisonn_.meazy.datagen.DatagenManager
import me.itzisonn_.meazy.datagen.deserializer.TokenTypeDeserializer
import me.itzisonn_.meazy.datagen.deserializer.TokenTypeSetDeserializer
import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.meazy.registry.defaultIdentifier
import me.itzisonn_.meazy.settings.SettingsManager.settings
import me.itzisonn_.meazy.text.TranslationsBundle
import me.itzisonn_.meazy.text.literal
import me.itzisonn_.meazy.text.translatable
import me.itzisonn_.meazy.util.logger.LogLevel
import me.itzisonn_.meazy.util.logger.Logger
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

        if (args.isEmpty()) {
            showAvailableCommandsList()
            return
        }

        val command = getByName(args[0])
        if (command == null) {
            Logger.log(LogLevel.ERROR, translatable("meazy:commands.unknown", args[0]))
            showAvailableCommandsList()
            return
        }

        val commandArgs = args.copyOfRange(1, args.size)
        if (commandArgs.size != command.args.size) {
            Logger.log(LogLevel.ERROR, translatable("meazy:commands.invalid_args", command.args.size, commandArgs.size))
            return
        }

        val log = command.execute(*commandArgs)
        if (log != null) {
            Logger.log(
                LogLevel.INFO,
                translatable(
                    "meazy:commands.loaded_info",
                    (endLoadMillis.toDouble() - startLoadMillis.toDouble()) / 1000
                ).append(literal(". ")).append(log)
            )
        }
    }

    private fun showAvailableCommandsList() {
        Logger.log(LogLevel.INFO, translatable("meazy:commands.available"))

        for (entry in Registries.COMMANDS.entries) {
            val command = entry.getValue()
            Logger.log(
                LogLevel.INFO,
                literal("    {0} {1}", command.name, command.args.joinToString(" ") { "<$it>" })
            )
        }
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