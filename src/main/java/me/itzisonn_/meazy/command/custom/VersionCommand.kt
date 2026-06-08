package me.itzisonn_.meazy.command.custom

import me.itzisonn_.meazy.MeazyMain
import me.itzisonn_.meazy.command.AbstractCommand
import me.itzisonn_.meazy.lang.text.Text
import me.itzisonn_.meazy.util.logger.LogLevel
import org.jspecify.annotations.NullMarked

@NullMarked
class VersionCommand : AbstractCommand("version", listOf()) {
    override fun execute(vararg args: String): Text? {
        MeazyMain.LOGGER.log(LogLevel.INFO, Text.translatable("meazy:commands.version", MeazyMain.VERSION))
        return null
    }
}
