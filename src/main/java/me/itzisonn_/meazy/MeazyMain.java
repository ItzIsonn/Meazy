package me.itzisonn_.meazy;

import lombok.Getter;
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
    @Getter
    private static final String version = "2.2";
    @Getter
    private static final Logger logger = LogManager.getLogger("meazy");

    private MeazyMain() {}

    public static void main(String[] args) {
        long startLoadMillis = System.currentTimeMillis();
        Registries.INIT();
        loadAddons();
        long endLoadMillis = System.currentTimeMillis();

        if (args.length == 0) {
            logger.log(Level.INFO, "Available commands:");
            for (RegistryEntry<Command> entry : Registries.COMMANDS.getEntries()) {
                Command command = entry.getValue();

                StringBuilder argsBuilder = new StringBuilder();
                for (int i = 0; i < command.getArgs().size(); i++) {
                    argsBuilder.append(command.getArgs().get(i));
                    if (i < command.getArgs().size() - 1) argsBuilder.append(" ");
                }

                logger.log(Level.INFO, "    {}", entry.getIdentifier().getId() + " " + argsBuilder);
            }
            return;
        }

        for (RegistryEntry<Command> entry : Registries.COMMANDS.getEntries()) {
            Command command = entry.getValue();
            if (entry.getIdentifier().getId().equals(args[0])) {
                if (args.length - 1 != command.getArgs().size()) {
                    logger.log(Level.ERROR, "Expected {} arguments but found {}", command.getArgs().size(), args.length - 1);
                    return;
                }

                String message = command.execute(Arrays.copyOfRange(args, 1, args.length));
                if (message != null) {
                    System.out.println();
                    logger.log(Level.INFO, "Loaded in {}s. {}", ((double) endLoadMillis - (double) startLoadMillis) / 1000, message);
                }
                return;
            }
        }


        logger.log(Level.ERROR, "Unknown command!");
        logger.log(Level.INFO, "Available commands:");
        for (RegistryEntry<Command> entry : Registries.COMMANDS.getEntries()) {
            logger.log(Level.INFO, "    {}", entry.getValue().toString());
        }
    }

    private static void loadAddons() {
        File addonsDir;
        try {
            addonsDir = new File(MeazyMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath() + "/addons/");
            if (!addonsDir.exists() && !addonsDir.mkdirs()) throw new RuntimeException("Can't load addons folder");
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        AddonManager addonManager = new AddonManager();
        for (Addon addon : addonManager.loadAddons(addonsDir)) {
            addonManager.enableAddon(addon);
        }

        int addons = addonManager.getAddons().length;
        if (addons == 1) logger.log(Level.INFO, "1 addon loaded");
        else logger.log(Level.INFO, "{} addons loaded", addons);
    }
}