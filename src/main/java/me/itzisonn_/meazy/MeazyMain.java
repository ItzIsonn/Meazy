package me.itzisonn_.meazy;

import me.itzisonn_.meazy.addon.AddonManager;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.addon.datagen.DatagenDeserializers;
import me.itzisonn_.meazy.command.Command;
import me.itzisonn_.meazy.command.Commands;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.lexer.TokenType;
import me.itzisonn_.meazy.lexer.TokenTypeSet;
import me.itzisonn_.meazy.parser.Parser;
import me.itzisonn_.meazy.parser.ast.Program;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.Interpreter;
import me.itzisonn_.meazy.version.Version;
import me.itzisonn_.registry.RegistryEntry;
import me.itzisonn_.registry.RegistryIdentifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public final class MeazyMain {
    public static final Version VERSION = Version.of("2.7");
    public static final Logger LOGGER = LogManager.getLogger("meazy");

    public static final AddonManager ADDON_MANAGER = new AddonManager();
    public static final File ADDONS_DIRECTORY = getAddonsDirectory();

    /**
     * Regex used by all identifiers
     */
    public static final String IDENTIFIER_REGEX = "[a-zA-Z_][a-zA-Z0-9_]*";

    private static boolean isInit = false;

    private MeazyMain() {}



    public static void main(String[] args) {
        long startLoadMillis = System.currentTimeMillis();
        INIT();
        long endLoadMillis = System.currentTimeMillis();

        if (args.length == 0) {
            showAvailableCommandsList();
            return;
        }

        Command command = Commands.getByName(args[0]);
        if (command == null) {
            LOGGER.log(Level.ERROR, "Unknown command with name {}", args[0]);
            showAvailableCommandsList();
            return;
        }

        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
        if (commandArgs.length != command.getArgs().size()) {
            LOGGER.log(Level.ERROR, "Expected {} arguments but found {}", command.getArgs().size(), commandArgs.length);
            return;
        }

        String message = command.execute(commandArgs);
        if (message != null) {
            LOGGER.log(Level.INFO, "Loaded in {}s. {}", ((double) endLoadMillis - (double) startLoadMillis) / 1000, message);
        }
    }

    private static void showAvailableCommandsList() {
        LOGGER.log(Level.INFO, "Available commands:");
        for (RegistryEntry<Command> entry : Registries.COMMANDS.getEntries()) {
            Command command = entry.getValue();
            LOGGER.log(Level.INFO, "    {}", command.getName() + " " + String.join(" ", command.getArgs()));
        }
    }

    private static File getAddonsDirectory() {
        try {
            File addonsDir = new File(new File(MeazyMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent() + "/addons/");
            if (!addonsDir.exists() && !addonsDir.mkdirs()) throw new RuntimeException("Can't load addons folder");
            return addonsDir;
        }
        catch (URISyntaxException e) {
            throw new RuntimeException("Can't load addons folder", e);
        }
    }

    public static void INIT() {
        if (isInit) throw new IllegalStateException("MeazyMain have already been initialized");
        isInit = true;

        Registries.INIT();

        loadAddons();
    }

    private static void loadAddons() {
        for (Addon addon : ADDON_MANAGER.loadAddons(ADDONS_DIRECTORY)) {
            ADDON_MANAGER.enableAddon(addon);

            for (TokenType tokenType : addon.getDatagenManager().getDeserializedMultiple("token_type", TokenType.class, DatagenDeserializers.getTokenTypeDeserializer())) {
                Registries.TOKEN_TYPES.register(RegistryIdentifier.of(addon.getAddonInfo().getId(), tokenType.getId()), tokenType);
            }

            for (TokenTypeSet tokenTypeSet : addon.getDatagenManager().getDeserializedSingle("token_type_set", TokenTypeSet.class, DatagenDeserializers.getTokenTypeSetDeserializer(addon))) {
                Registries.TOKEN_TYPE_SETS.register(RegistryIdentifier.of(addon.getAddonInfo().getId(), tokenTypeSet.getId()), tokenTypeSet);
            }

            addon.afterDataLoaded();

            for (String lines : addon.getDatagenManager().getDatagenFilesLines("program")) {
                List<Token> tokens = Registries.TOKENIZATION_FUNCTION.getEntry().getValue().apply(lines);

                Parser.reset();
                Program program = Registries.PARSE_TOKENS_FUNCTION.getEntry().getValue().apply(null, tokens);

                GlobalEnvironment globalEnvironment = Registries.GLOBAL_ENVIRONMENT_FACTORY.getEntry().getValue().create(program.getFile());
                Interpreter.evaluate(program, globalEnvironment);
                Registries.NATIVE_RELATED_GLOBAL_ENVIRONMENTS.add(globalEnvironment);
            }
        }

        int addons = ADDON_MANAGER.getAddons().length;
        if (addons == 1) LOGGER.log(Level.INFO, "1 addon loaded");
        else LOGGER.log(Level.INFO, "{} addons loaded", addons);
    }
}