package me.itzisonn_.meazy;

import me.itzisonn_.meazy.addon.AddonManager;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.command.Command;
import me.itzisonn_.meazy.command.Commands;
import me.itzisonn_.registry.RegistryEntry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Arrays;

public final class MeazyMain {
    public static final String VERSION = "2.6";
    public static final Logger LOGGER = LogManager.getLogger("meazy");

    public static final AddonManager ADDON_MANAGER = new AddonManager();
    public static final File ADDONS_DIRECTORY = getAddonsDirectory();

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
        if (isInit) throw new IllegalStateException("MeazyMain have already been initialized!");
        isInit = true;

        Registries.INIT();

        loadAddons();
    }

    private static void loadAddons() {
        for (Addon addon : ADDON_MANAGER.loadAddons(ADDONS_DIRECTORY)) {
            ADDON_MANAGER.enableAddon(addon);
        }

        int addons = ADDON_MANAGER.getAddons().length;
        if (addons == 1) LOGGER.log(Level.INFO, "1 addon loaded");
        else LOGGER.log(Level.INFO, "{} addons loaded", addons);
    }
}