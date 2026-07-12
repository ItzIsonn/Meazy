package me.itzisonn_.meazy.command.custom

import me.itzisonn_.meazy.MeazyMain
import me.itzisonn_.meazy.command.Command
import me.itzisonn_.meazy.command.CommandResult
import me.itzisonn_.meazy.util.text.translatable

val versionCommand = Command("version") {
    executes {
        return@executes CommandResult.Success(
            translatable("meazy:commands.version", MeazyMain.VERSION)
        )
    }
}