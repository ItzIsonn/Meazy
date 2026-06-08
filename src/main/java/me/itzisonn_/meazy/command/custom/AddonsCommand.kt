package me.itzisonn_.meazy.command.custom

import me.itzisonn_.meazy.MeazyMain
import me.itzisonn_.meazy.command.AbstractCommand
import me.itzisonn_.meazy.lang.text.Text
import me.itzisonn_.meazy.util.logger.LogLevel

class AddonsCommand : AbstractCommand("addons", mutableListOf()) {
    override fun execute(vararg args: String): Text? {
        if (MeazyMain.ADDON_MANAGER.getAddons().isEmpty()) {
            MeazyMain.LOGGER.log(LogLevel.INFO, Text.translatable("meazy:commands.addons.empty"))
            return null
        }

        MeazyMain.LOGGER.log(LogLevel.INFO, Text.translatable("meazy:commands.addons.loaded"))
        for (addon in MeazyMain.ADDON_MANAGER.getAddons()) {
            val addonInfo = addon.getAddonInfo()
            val authors = if (!addonInfo.authors.isEmpty())
                " " + Text.translatable("meazy:commands.addons.by") + " " + addonInfo.authors.joinToString(", ")
            else ""

            val description = if (!addonInfo.description.isBlank()) {
                " - " + addonInfo.description
            }
            else ""

            MeazyMain.LOGGER.log(LogLevel.INFO, Text.literal("    " + addonInfo.fullName + authors + description))
        }

        return null
    }
}
