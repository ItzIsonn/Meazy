package me.itzisonn_.meazy;

import lombok.Getter;
import me.itzisonn_.meazy.addon.AddonManager;
import me.itzisonn_.meazy.command.AbstractCommand;
import me.itzisonn_.meazy.command.Commands;
import me.itzisonn_.meazy.lang.Language;
import me.itzisonn_.meazy.lang.file_provider.LanguageFileProvider;
import me.itzisonn_.meazy.lang.bundle.BundleManager;
import me.itzisonn_.meazy.lang.file_provider.LanguageFileProviderImpl;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.settings.SettingsManager;
import me.itzisonn_.meazy.version.Version;
import me.itzisonn_.registry.RegistryEntry;
import me.itzisonn_.registry.RegistryIdentifier;
import org.apache.logging.log4j.Level;

import java.util.Arrays;

public final class MeazyMain {
    public static final Version VERSION = Version.of("2.7");
    public static final MeazyLogger LOGGER = new MeazyLogger();

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
            LOGGER.log(Level.ERROR, Text.translatable("meazy:commands.unknown", args[0]));
            showAvailableCommandsList();
            return;
        }

        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
        if (commandArgs.length != command.getArgs().size()) {
            LOGGER.log(Level.ERROR, Text.translatable("meazy:commands.invalid_args", command.getArgs().size(), commandArgs.length));
            return;
        }

        Text log = command.execute(commandArgs);
        if (log != null) {
            LOGGER.log(Level.INFO, Text.translatable("meazy:commands.loaded_info", ((double) endLoadMillis - (double) startLoadMillis) / 1000).append(log));
        }
    }

    private static void showAvailableCommandsList() {
        LOGGER.log(Level.INFO, Text.translatable("meazy:commands.available"));

        for (RegistryEntry<AbstractCommand> entry : Registries.COMMANDS.getEntries()) {
            AbstractCommand command = entry.getValue();
            LOGGER.log(Level.INFO, Text.literal("    " + command.getName() + " " + String.join(" ", command.getArgs())));
        }
    }



    public static void INITIALIZE() {
        if (isInitialized) throw new IllegalStateException("MeazyMain have already been initialized");
        isInitialized = true;

        Registries.INIT();

        String stringLanguage = SETTINGS_MANAGER.getSettings().getLanguage();
        RegistryEntry<Language> languagesEntry = Registries.LANGUAGES.getEntry(stringLanguage);
        if (languagesEntry == null) MeazyMain.LOGGER.log(Level.ERROR, Text.translatable("meazy:settings.unknown_language", stringLanguage));
        else BUNDLE_MANAGER.setLanguage(languagesEntry.getValue());

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
     * @throws NullPointerException If id is null
     * @throws IllegalArgumentException If id doesn't match {@link RegistryIdentifier#IDENTIFIER_REGEX}
     */
    public static RegistryIdentifier getDefaultIdentifier(String id) throws NullPointerException, IllegalArgumentException {
        return RegistryIdentifier.of("meazy", id);
    }
}