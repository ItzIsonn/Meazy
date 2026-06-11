package me.itzisonn_.meazy

import me.itzisonn_.meazy.command.Commands.getByName
import me.itzisonn_.meazy.datagen.DatagenDeserializers
import me.itzisonn_.meazy.datagen.DatagenManager
import me.itzisonn_.meazy.lexer.TokenType
import me.itzisonn_.meazy.lexer.TokenTypeSet
import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.meazy.settings.SettingsManager.settings
import me.itzisonn_.meazy.text.TranslationsBundle
import me.itzisonn_.meazy.text.literal
import me.itzisonn_.meazy.text.translatable
import me.itzisonn_.meazy.util.logger.LogLevel
import me.itzisonn_.meazy.util.logger.Logger
import me.itzisonn_.meazy.version.Version
import me.itzisonn_.registry.RegistryIdentifier

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

        for (tokenType in DatagenManager.getDeserializedMultiple("token_type", TokenType::class, DatagenDeserializers.tokenTypeDeserializer)) {
            val id = getDefaultIdentifier(tokenType.id)
            if (Registries.TOKEN_TYPES.getEntry(id) != null) continue
            Registries.TOKEN_TYPES.register(getDefaultIdentifier(tokenType.id), tokenType)
        }

        for (tokenTypeSet in DatagenManager.getDeserializedSingle("token_type_set", TokenTypeSet::class, DatagenDeserializers.tokenTypeSetDeserializer)) {
            Registries.TOKEN_TYPE_SETS.register(getDefaultIdentifier(tokenTypeSet.id), tokenTypeSet)
        }
    }

    /**
     * Creates new RegistryIdentifier with 'meazy' namespace
     * 
     * @param id Identifier's id that matches [RegistryIdentifier.IDENTIFIER_REGEX]
     * @return New RegistryIdentifier
     * 
     * @apiNote Recommended to use [RegistryIdentifier.of] or [RegistryIdentifier.of]
     * because 'meazy' namespace belongs to core identifiers
     * 
     * @throws IllegalArgumentException If id doesn't match [RegistryIdentifier.IDENTIFIER_REGEX]
     */
    @JvmStatic
    fun getDefaultIdentifier(id: String): RegistryIdentifier {
        return RegistryIdentifier.of("meazy", id)
    }
}