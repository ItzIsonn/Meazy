package me.itzisonn_.meazy;

import lombok.Getter;
import me.itzisonn_.meazy.addon.AddonManager;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.addon.datagen.DatagenDeserializers;
import me.itzisonn_.meazy.command.Command;
import me.itzisonn_.meazy.command.Commands;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.lang.Language;
import me.itzisonn_.meazy.lang.file_provider.LanguageFileProvider;
import me.itzisonn_.meazy.lang.bundle.BundleManager;
import me.itzisonn_.meazy.lang.file_provider.LanguageFileProviderImpl;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.lexer.TokenType;
import me.itzisonn_.meazy.lexer.TokenTypeSet;
import me.itzisonn_.meazy.parser.ast.Program;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.settings.SettingsManager;
import me.itzisonn_.meazy.version.Version;
import me.itzisonn_.registry.RegistryEntry;
import me.itzisonn_.registry.RegistryIdentifier;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public final class MeazyMain {
    public static final Version VERSION = Version.of("2.7");
    public static final MeazyLogger LOGGER = new MeazyLogger();

    public static final LanguageFileProvider MEAZY_LANGUAGE_FILE_PROVIDER = new LanguageFileProviderImpl("meazy", MeazyMain.class.getClassLoader()::getResourceAsStream);
    public static final BundleManager BUNDLE_MANAGER = new BundleManager(MEAZY_LANGUAGE_FILE_PROVIDER);

    public static final SettingsManager SETTINGS_MANAGER = new SettingsManager();
    public static final AddonManager ADDON_MANAGER = new AddonManager(getAddonsDirectory());

    /**
     * Regex used by all identifiers
     */
    public static final String IDENTIFIER_REGEX = "[a-zA-Z_][a-zA-Z0-9_]*";

    @Getter
    private static boolean isInitialized = false;

    private MeazyMain() {}



    public static void main(String[] args) {
        long startLoadMillis = System.currentTimeMillis();
        INITIALIZE();
        long endLoadMillis = System.currentTimeMillis();

        if (args.length == 0) {
            showAvailableCommandsList();
            return;
        }

        Command command = Commands.getByName(args[0]);
        if (command == null) {
            LOGGER.log(Level.ERROR, Text.translatable("meazy:commands.unknown"), args[0]);
            showAvailableCommandsList();
            return;
        }

        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
        if (commandArgs.length != command.getArgs().size()) {
            LOGGER.log(Level.ERROR, Text.translatable("meazy:commands.invalid_args"), command.getArgs().size(), commandArgs.length);
            return;
        }

        String message = command.execute(commandArgs);
        if (message != null) {
            LOGGER.log(Level.INFO, Text.translatable("meazy:commands.loaded_info"), ((double) endLoadMillis - (double) startLoadMillis) / 1000, message);
        }
    }

    private static void showAvailableCommandsList() {
        LOGGER.log(Level.INFO, Text.translatable("meazy:commands.available"));
        for (RegistryEntry<Command> entry : Registries.COMMANDS.getEntries()) {
            Command command = entry.getValue();
            LOGGER.log(Level.INFO, Text.translatable("    " + command.getName() + " " + String.join(" ", command.getArgs())));
        }
    }



    public static void INITIALIZE() {
        if (isInitialized) throw new IllegalStateException("MeazyMain have already been initialized");
        isInitialized = true;

        Registries.INIT();

        String stringLanguage = SETTINGS_MANAGER.getSettings().getLanguage();
        RegistryEntry<Language> languagesEntry = Registries.LANGUAGES.getEntry(stringLanguage);
        if (languagesEntry == null) MeazyMain.LOGGER.log(Level.ERROR, Text.translatable("meazy:settings.unknown_language"), stringLanguage);
        else BUNDLE_MANAGER.setLanguage(languagesEntry.getValue());

        loadAddons();
    }



    private static File getAddonsDirectory() {
        File addonsDir;
        try {
            addonsDir = new File(new File(MeazyMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent() + "/addons/");
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(Text.translatable("meazy:addons.cant_load_folder").getContent(), e);
        }

        if (!addonsDir.exists() && !addonsDir.mkdirs()) throw new RuntimeException(Text.translatable("meazy:addons.cant_load_folder").getContent());
        return addonsDir;
    }

    private static void loadAddons() {
        for (Addon addon : ADDON_MANAGER.loadAddons()) {
            if (addon.getLanguageFileProvider() != null) BUNDLE_MANAGER.addLanguageFileProvider(addon.getLanguageFileProvider());
            ADDON_MANAGER.enableAddon(addon);

            for (TokenType tokenType : addon.getDatagenManager().getDeserializedMultiple("token_type", TokenType.class, DatagenDeserializers.getTokenTypeDeserializer())) {
                Registries.TOKEN_TYPES.register(RegistryIdentifier.of(addon.getAddonInfo().getId(), tokenType.getId()), tokenType);
            }

            for (TokenTypeSet tokenTypeSet : addon.getDatagenManager().getDeserializedSingle("token_type_set", TokenTypeSet.class, DatagenDeserializers.getTokenTypeSetDeserializer(addon))) {
                Registries.TOKEN_TYPE_SETS.register(RegistryIdentifier.of(addon.getAddonInfo().getId(), tokenTypeSet.getId()), tokenTypeSet);
            }

            addon.afterDataLoaded();

            RuntimeContext context = new RuntimeContext();
            GlobalEnvironment globalEnvironment = context.getGlobalEnvironment();

            for (String lines : addon.getDatagenManager().getDatagenFilesLines("program")) {
                List<Token> tokens = Registries.TOKENIZATION_FUNCTION.getEntry().getValue().tokenize(lines);
                Program program = Registries.PARSE_TOKENS_FUNCTION.getEntry().getValue().parse(null, tokens);

                FileEnvironment fileEnvironment = Registries.EVALUATE_PROGRAM_FUNCTION.getEntry().getValue().evaluate(program, globalEnvironment);
                globalEnvironment.addFileEnvironment(fileEnvironment);
                Registries.NATIVE_FILE_ENVIRONMENTS.add(fileEnvironment);
            }
        }

        int addons = ADDON_MANAGER.getAddons().length;
        if (addons == 1) LOGGER.log(Level.INFO, Text.translatable("meazy:addons.single_loaded"));
        else LOGGER.log(Level.INFO, Text.translatable("meazy:addons.multiple_loaded"), addons);
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