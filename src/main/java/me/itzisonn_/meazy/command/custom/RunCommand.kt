package me.itzisonn_.meazy.command.custom

import me.itzisonn_.meazy.command.AbstractCommand
import me.itzisonn_.meazy.text.Text
import me.itzisonn_.meazy.text.translatable
import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.meazy.logger.LogLevel
import me.itzisonn_.meazy.logger.Logger
import java.io.File

class RunCommand : AbstractCommand("run", listOf("target_file")) {
    override fun execute(vararg args: String): Text? {
        val file = File(args[0])
        if (file.isDirectory() || !file.exists()) {
            Logger.log(LogLevel.ERROR, translatable("meazy:file.doesnt_exist", file.absolutePath))
            return null
        }

        val extension = file.extension
        if (extension != "mea") {
            Logger.log(LogLevel.ERROR, translatable("meazy:file.unsupported_extension", extension))
            return null
        }

        Logger.log(LogLevel.INFO, translatable("meazy:commands.run.running", file.absolutePath))
        val startMillis = System.currentTimeMillis()

        val tokens = Registries.tokenizationFunction(file.readText())
        val program = Registries.parseTokensFunction(file, tokens)

        val classes = Registries.compileProgramFunction(program)
        Registries.runProgramFunction(classes)

        val endMillis = System.currentTimeMillis()
        return translatable("meazy:commands.run.info", (endMillis - startMillis).toDouble() / 1000)
    }
}
