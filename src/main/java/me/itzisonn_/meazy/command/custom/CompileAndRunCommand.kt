package me.itzisonn_.meazy.command.custom

import me.itzisonn_.meazy.MeazyMain
import me.itzisonn_.meazy.command.AbstractCommand
import me.itzisonn_.meazy.lang.text.Text
import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.meazy.util.FileUtils.getExtension
import me.itzisonn_.meazy.util.FileUtils.getLines
import me.itzisonn_.meazy.util.logger.LogLevel
import org.jspecify.annotations.NullMarked
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*

@NullMarked
class CompileAndRunCommand :
    AbstractCommand("compile_and_run", mutableListOf("<target_file>", "<output_directory_path>")) {
    override fun execute(vararg args: String): Text? {
        val file = File(args[0])
        if (file.isDirectory() || !file.exists()) {
            MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:file.doesnt_exist", file.getAbsolutePath()))
            return null
        }

        val extension = getExtension(file)
        if (extension != "mea") {
            MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:file.unsupported_extension", extension))
            return null
        }

        MeazyMain.LOGGER.log(
            LogLevel.INFO,
            Text.translatable("meazy:commands.compile.compiling", file.getAbsolutePath())
        )
        val startCompileMillis = System.currentTimeMillis()

        val tokens = Registries.TOKENIZATION_FUNCTION.getEntry().getValue().tokenize(getLines(file))
        val program = Registries.PARSE_TOKENS_FUNCTION.getEntry().getValue().parse(file, tokens)
        val classes = Registries.COMPILE_PROGRAM_FUNCTION.getEntry().getValue().compile(program)

        val outputDirectory = File(args[1])
        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                throw RuntimeException("Failed to create output directory") //TODO
            }
        }
        else Arrays.stream<File>(outputDirectory.listFiles()).forEach { obj: File? -> obj!!.delete() }

        for (classDesc in classes.keys) {
            val classFile: ByteArray = classes.get(classDesc)!!
            val outputFile = File(outputDirectory, classDesc.displayName() + ".class")

            try {
                Files.write(outputFile.toPath(), classFile)
            }
            catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        val endCompileMillis = System.currentTimeMillis()
        MeazyMain.LOGGER.log(
            LogLevel.INFO,
            Text.translatable("meazy:commands.compile.info", (endCompileMillis - startCompileMillis).toDouble() / 1000)
        )

        MeazyMain.LOGGER.log(LogLevel.INFO, Text.translatable("meazy:commands.run.running", file.getAbsolutePath()))
        val startRunMillis = System.currentTimeMillis()
        Registries.RUN_PROGRAM_FUNCTION.getEntry().getValue().run(classes)

        val endRunMillis = System.currentTimeMillis()
        return Text.translatable("meazy:commands.run.info", (endRunMillis - startRunMillis).toDouble() / 1000)
    }
}
