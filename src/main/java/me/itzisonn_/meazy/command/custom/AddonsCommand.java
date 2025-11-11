package me.itzisonn_.meazy.command.custom;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.addon.AddonInfo;
import me.itzisonn_.meazy.addon.AddonManager;
import me.itzisonn_.meazy.command.AbstractCommand;
import me.itzisonn_.meazy.lang.text.Text;
import org.apache.logging.log4j.Level;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.List;

public class AddonsCommand extends AbstractCommand {
    public AddonsCommand() {
        super("addons", List.of("[list | downloadDefault]"));
    }

    @Override
    public Text execute(String[] args) {
        switch (args[0]) {
            case "list" -> {
                if (MeazyMain.ADDON_MANAGER.getAddons().isEmpty()) {
                    MeazyMain.LOGGER.log(Level.INFO, Text.translatable("meazy:commands.addons.empty"));
                    return null;
                }

                MeazyMain.LOGGER.log(Level.INFO, Text.translatable("meazy:commands.addons.loaded"));
                for (Addon addon : MeazyMain.ADDON_MANAGER.getAddons()) {
                    AddonInfo addonInfo = addon.getAddonInfo();

                    String authors;
                    if (!addonInfo.getAuthors().isEmpty()) {
                        authors = " " + Text.translatable("meazy:commands.addons.by") + " " + String.join(", ", addonInfo.getAuthors());
                    }
                    else authors = "";

                    String description;
                    if (!addonInfo.getDescription().isBlank()) {
                        description = " - " + addonInfo.getDescription();
                    }
                    else description = "";

                    MeazyMain.LOGGER.log(Level.INFO, Text.literal("    " + addonInfo.getFullName() + authors + description));
                }
                return null;
            }

            case "downloadDefault" -> {
                String site = "https://github.com/ItzIsonn/MeazyAddon/releases/download/v" + MeazyMain.VERSION + "/MeazyAddon-v" + MeazyMain.VERSION + ".jar";
                String file = AddonManager.ADDONS_FOLDER.getAbsolutePath() + "\\" + Arrays.asList(site.split("/")).getLast();

                ReadableByteChannel byteChannel;
                try {
                    URL url = new URI(site).toURL();
                    byteChannel = Channels.newChannel(url.openStream());
                }
                catch (FileNotFoundException e) {
                    MeazyMain.LOGGER.log(Level.ERROR, Text.translatable("meazy:commands.addons.cant_find_default", MeazyMain.VERSION));
                    return null;
                }
                catch (URISyntaxException | IOException e) {
                    throw new RuntimeException(e);
                }

                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    fileOutputStream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }

                MeazyMain.LOGGER.log(Level.INFO, Text.translatable("meazy:commands.addons.loaded_default", MeazyMain.VERSION));
                return null;
            }

            default -> {
                MeazyMain.LOGGER.log(Level.ERROR, Text.translatable("meazy:commands.invalid_argument", args[0]));
                return null;
            }
        }
    }
}
