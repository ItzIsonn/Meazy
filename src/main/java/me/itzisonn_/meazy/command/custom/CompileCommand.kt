package me.itzisonn_.meazy.command.custom

import me.itzisonn_.meazy.MeazyMain
import me.itzisonn_.meazy.command.AbstractCommand
import me.itzisonn_.meazy.text.Text
import me.itzisonn_.meazy.text.translatable
import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.meazy.util.FileUtils.getExtension
import me.itzisonn_.meazy.util.FileUtils.getLines
import me.itzisonn_.meazy.util.logger.LogLevel
import java.io.File
import java.io.IOException
import java.nio.file.Files

class CompileCommand : AbstractCommand(
    "compile",
    listOf("target_file", "output_directory_path")
) {
    override fun execute(vararg args: String): Text? {
        val file = File(args[0])
        if (file.isDirectory() || !file.exists()) {
            MeazyMain.LOGGER.log(LogLevel.ERROR, translatable("meazy:file.doesnt_exist", file.absolutePath))
            return null
        }

        val extension = getExtension(file)
        if (extension != "mea") {
            MeazyMain.LOGGER.log(LogLevel.ERROR, translatable("meazy:file.unsupported_extension", extension))
            return null
        }

        MeazyMain.LOGGER.log(
            LogLevel.INFO,
            translatable("meazy:commands.compile.compiling", file.absolutePath)
        )
        val startMillis = System.currentTimeMillis()

        val tokens = Registries.TOKENIZATION_FUNCTION.getEntry().getValue().tokenize(getLines(file))
        val program = Registries.PARSE_TOKENS_FUNCTION.getEntry().getValue().parse(file, tokens)
        val classes = Registries.COMPILE_PROGRAM_FUNCTION.getEntry().getValue().compile(program)

        val outputDirectory = File(args[1])
        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                throw RuntimeException("Failed to create output directory") //TODO translation
            }
        }

        for (classDesc in classes.keys) {
            val classFile: ByteArray = classes[classDesc]!!
            val outputFile = File(outputDirectory, classDesc.displayName() + ".class")

            try {
                Files.write(outputFile.toPath(), classFile)
            }
            catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        val endMillis = System.currentTimeMillis()
        return translatable("meazy:commands.compile.info", (endMillis - startMillis).toDouble() / 1000)
    }
}
