package me.itzisonn_.meazy.command.custom

import me.itzisonn_.meazy.MeazyMain
import me.itzisonn_.meazy.command.AbstractCommand
import me.itzisonn_.meazy.text.Text
import me.itzisonn_.meazy.text.translatable
import me.itzisonn_.meazy.util.logger.LogLevel

class VersionCommand : AbstractCommand("version", listOf()) {
    override fun execute(vararg args: String): Text? {
        MeazyMain.LOGGER.log(LogLevel.INFO, translatable("meazy:commands.version", MeazyMain.VERSION))
        return null
    }
}
