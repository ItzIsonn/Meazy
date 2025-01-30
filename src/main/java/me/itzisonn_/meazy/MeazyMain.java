package me.itzisonn_.meazy;

import me.itzisonn_.meazy.addons.AddonManager;
import me.itzisonn_.meazy.addons.Addon;
import me.itzisonn_.meazy.command.Command;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryEntry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Arrays;

public final class MeazyMain {
    public static final String VERSION = "2.3";
    public static final Logger LOGGER = LogManager.getLogger("meazy");
    public static final AddonManager ADDON_MANAGER = new AddonManager();
    private static boolean isInit = false;

    private MeazyMain() {}



    public static void main(String[] args) {
        long startLoadMillis = System.currentTimeMillis();
        INIT();
        long endLoadMillis = System.currentTimeMillis();

        if (args.length == 0) {
            LOGGER.log(Level.INFO, "Available commands:");
            for (RegistryEntry<Command> entry : Registries.COMMANDS.getEntries()) {
                Command command = entry.getValue();

                StringBuilder argsBuilder = new StringBuilder();
                for (int i = 0; i < command.getArgs().size(); i++) {
                    argsBuilder.append(command.getArgs().get(i));
                    if (i < command.getArgs().size() - 1) argsBuilder.append(" ");
                }

                LOGGER.log(Level.INFO, "    {}", entry.getIdentifier().getId() + " " + argsBuilder);
            }
            return;
        }

        for (RegistryEntry<Command> entry : Registries.COMMANDS.getEntries()) {
            Command command = entry.getValue();
            if (entry.getIdentifier().getId().equals(args[0])) {
                if (args.length - 1 != command.getArgs().size()) {
                    LOGGER.log(Level.ERROR, "Expected {} arguments but found {}", command.getArgs().size(), args.length - 1);
                    return;
                }

                String message = command.execute(Arrays.copyOfRange(args, 1, args.length));
                if (message != null) {
                    LOGGER.log(Level.INFO, "Loaded in {}s. {}", ((double) endLoadMillis - (double) startLoadMillis) / 1000, message);
                }
                return;
            }
        }


        LOGGER.log(Level.ERROR, "Unknown command!");
        LOGGER.log(Level.INFO, "Available commands:");
        for (RegistryEntry<Command> entry : Registries.COMMANDS.getEntries()) {
            Command command = entry.getValue();

            StringBuilder argsBuilder = new StringBuilder();
            for (int i = 0; i < command.getArgs().size(); i++) {
                argsBuilder.append(command.getArgs().get(i));
                if (i < command.getArgs().size() - 1) argsBuilder.append(" ");
            }

            LOGGER.log(Level.INFO, "    {}", entry.getIdentifier().getId() + " " + argsBuilder);
        }
    }

    private static void loadAddons() {
        File addonsDir;
        try {
            addonsDir = new File(new File(MeazyMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent() + "/addons/");
            if (!addonsDir.exists() && !addonsDir.mkdirs()) throw new RuntimeException("Can't load addons folder");
        }
        catch (URISyntaxException e) {
            throw new RuntimeException("Can't load addons folder", e);
        }

        for (Addon addon : ADDON_MANAGER.loadAddons(addonsDir)) {
            ADDON_MANAGER.enableAddon(addon);
        }

        int addons = ADDON_MANAGER.getAddons().length;
        if (addons == 1) LOGGER.log(Level.INFO, "1 addon loaded");
        else LOGGER.log(Level.INFO, "{} addons loaded", addons);
    }

    public static void INIT() {
        if (isInit) throw new IllegalStateException("MeazyMain have already been initialized!");
        isInit = true;
        Registries.INIT();
        loadAddons();
    }
}