package me.itzisonn_.meazy;

import lombok.Getter;
import me.itzisonn_.meazy.addon.AddonManager;
import me.itzisonn_.meazy.datagen.DatagenDeserializers;
import me.itzisonn_.meazy.command.AbstractCommand;
import me.itzisonn_.meazy.command.Commands;
import me.itzisonn_.meazy.datagen.manager.MeazyDatagenManager;
import me.itzisonn_.meazy.lang.Language;
import me.itzisonn_.meazy.lang.file_provider.LanguageFileProvider;
import me.itzisonn_.meazy.lang.bundle.BundleManager;
import me.itzisonn_.meazy.lang.file_provider.LanguageFileProviderImpl;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.TokenType;
import me.itzisonn_.meazy.lexer.TokenTypeSet;
import me.itzisonn_.meazy.util.logger.LogLevel;
import me.itzisonn_.meazy.util.logger.Logger;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.settings.SettingsManager;
import me.itzisonn_.meazy.version.Version;
import me.itzisonn_.registry.RegistryEntry;
import me.itzisonn_.registry.RegistryIdentifier;

import java.util.Arrays;

public final class MeazyMain {
    public static final Version VERSION = Version.of("3.0");
    public static final Logger LOGGER = new Logger("meazy");

    public static final LanguageFileProvider MEAZY_LANGUAGE_FILE_PROVIDER = new LanguageFileProviderImpl("meazy", MeazyMain.class.getClassLoader()::getResourceAsStream);
    public static final BundleManager BUNDLE_MANAGER = new BundleManager(MEAZY_LANGUAGE_FILE_PROVIDER);

    public static final SettingsManager SETTINGS_MANAGER = new SettingsManager();
    public static final AddonManager ADDON_MANAGER = new AddonManager();

    /**
     * Regex used by all identifiers
     */
    public static final String IDENTIFIER_REGEX = "[a-zA-Z_][a-zA-Z0-9_]*";

    @Getter
    private static boolean isInitialized = false;

    private MeazyMain() {}



    static void main(String[] args) {
        long startLoadMillis = System.currentTimeMillis();
        INITIALIZE();
        long endLoadMillis = System.currentTimeMillis();

        if (args.length == 0) {
            showAvailableCommandsList();
            return;
        }

        AbstractCommand command = Commands.getByName(args[0]);
        if (command == null) {
            LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:commands.unknown", args[0]));
            showAvailableCommandsList();
            return;
        }

        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
        if (commandArgs.length != command.getArgs().size()) {
            LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:commands.invalid_args", command.getArgs().size(), commandArgs.length));
            return;
        }

        Text log = command.execute(commandArgs);
        if (log != null) {
            LOGGER.log(LogLevel.INFO, Text.translatable("meazy:commands.loaded_info", ((double) endLoadMillis - (double) startLoadMillis) / 1000).append(Text.literal(". ")).append(log));
        }
    }

    private static void showAvailableCommandsList() {
        LOGGER.log(LogLevel.INFO, Text.translatable("meazy:commands.available"));

        for (RegistryEntry<AbstractCommand> entry : Registries.COMMANDS.getEntries()) {
            AbstractCommand command = entry.getValue();
            LOGGER.log(LogLevel.INFO, Text.literal("    " + command.getName() + " " + String.join(" ", command.getArgs())));
        }
    }



    public static void INITIALIZE() {
        if (isInitialized) throw new IllegalStateException("MeazyMain have already been initialized");
        isInitialized = true;

        Registries.INIT();

        String stringLanguage = SETTINGS_MANAGER.getSettings().getLanguage();
        RegistryEntry<Language> languagesEntry = Registries.LANGUAGES.getEntry(stringLanguage);
        if (languagesEntry == null) LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:settings.unknown_language", stringLanguage));
        else BUNDLE_MANAGER.setLanguage(languagesEntry.getValue());

        MeazyDatagenManager meazyDatagenManager = new MeazyDatagenManager();

        for (TokenType tokenType : meazyDatagenManager.getDeserializedMultiple("token_type", TokenType.class, DatagenDeserializers.getTokenTypeDeserializer())) {
            Registries.TOKEN_TYPES.register(getDefaultIdentifier(tokenType.getId()), tokenType);
        }

        for (TokenTypeSet tokenTypeSet : meazyDatagenManager.getDeserializedSingle("token_type_set", TokenTypeSet.class, DatagenDeserializers.getTokenTypeSetDeserializer("meazy"))) {
            Registries.TOKEN_TYPE_SETS.register(getDefaultIdentifier(tokenTypeSet.getId()), tokenTypeSet);
        }

        ADDON_MANAGER.enableAddons();
    }

    /**
     * Creates new RegistryIdentifier with 'meazy' namespace
     *
     * @param id Identifier's id that matches {@link RegistryIdentifier#IDENTIFIER_REGEX}
     * @return New RegistryIdentifier
     *
     * @apiNote Recommended to use {@link RegistryIdentifier#of(String, String)} or {@link RegistryIdentifier#of(String)}
     *          because 'meazy' namespace belongs to core identifiers
     *
     * @throws IllegalArgumentException If id doesn't match {@link RegistryIdentifier#IDENTIFIER_REGEX}
     */
    public static RegistryIdentifier getDefaultIdentifier(String id) throws IllegalArgumentException {
        return RegistryIdentifier.of("meazy", id);
    }
}