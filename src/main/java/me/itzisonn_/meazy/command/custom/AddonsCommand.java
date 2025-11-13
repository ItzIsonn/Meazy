package me.itzisonn_.meazy.command.custom;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.addon.AddonInfo;
import me.itzisonn_.meazy.command.AbstractCommand;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.logging.LogLevel;

import java.util.List;

public class AddonsCommand extends AbstractCommand {
    public AddonsCommand() {
        super("addons", List.of());
    }

    @Override
    public Text execute(String[] args) {
        if (MeazyMain.ADDON_MANAGER.getAddons().isEmpty()) {
            MeazyMain.LOGGER.log(LogLevel.INFO, Text.translatable("meazy:commands.addons.empty"));
            return null;
        }

        MeazyMain.LOGGER.log(LogLevel.INFO, Text.translatable("meazy:commands.addons.loaded"));
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

            MeazyMain.LOGGER.log(LogLevel.INFO, Text.literal("    " + addonInfo.getFullName() + authors + description));
        }

        return null;
    }
}
