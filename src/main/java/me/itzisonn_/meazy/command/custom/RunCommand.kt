package me.itzisonn_.meazy.command.custom

import me.itzisonn_.meazy.MeazyMain
import me.itzisonn_.meazy.command.AbstractCommand
import me.itzisonn_.meazy.lang.text.Text
import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.meazy.util.FileUtils.getExtension
import me.itzisonn_.meazy.util.FileUtils.getLines
import me.itzisonn_.meazy.util.logger.LogLevel
import java.io.File

class RunCommand : AbstractCommand("run", listOf("target_file")) {
    override fun execute(vararg args: String): Text? {
        val file = File(args[0])
        if (file.isDirectory() || !file.exists()) {
            MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:file.doesnt_exist", file.absolutePath))
            return null
        }

        val extension = getExtension(file)
        if (extension != "mea") {
            MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:file.unsupported_extension", extension))
            return null
        }

        MeazyMain.LOGGER.log(LogLevel.INFO, Text.translatable("meazy:commands.run.running", file.absolutePath))
        val startMillis = System.currentTimeMillis()

        val tokens = Registries.TOKENIZATION_FUNCTION.getEntry().getValue().tokenize(getLines(file))
        val program = Registries.PARSE_TOKENS_FUNCTION.getEntry().getValue().parse(file, tokens)

        val classes = Registries.COMPILE_PROGRAM_FUNCTION.getEntry().getValue().compile(program)
        Registries.RUN_PROGRAM_FUNCTION.getEntry().getValue().run(classes)

        val endMillis = System.currentTimeMillis()
        return Text.translatable("meazy:commands.run.info", (endMillis - startMillis).toDouble() / 1000)
    }
}
