package me.itzisonn_.meazy.command.custom

import me.itzisonn_.meazy.command.Command
import me.itzisonn_.meazy.command.CommandResult
import me.itzisonn_.meazy.command.StringType
import me.itzisonn_.meazy.runtime.RuntimeFunctions
import me.itzisonn_.meazy.util.text.translatable
import me.itzisonn_.meazy.util.logger.LogLevel
import me.itzisonn_.meazy.util.logger.Logger
import java.io.File
import java.io.IOException
import java.nio.file.Files

val compileAndRunCommand = Command("compile_and_run") {
    argument("target_file", StringType) { targetArg ->
        argument("output_directory", StringType) { outputArg ->
            executes {
                val file = File(getArgument(targetArg))
                if (file.isDirectory() || !file.exists()) {
                    return@executes CommandResult.Failure(
                        translatable("file.doesnt_exist", file.absolutePath)
                    )
                }

                val extension = file.extension
                if (extension != "mea") {
                    return@executes CommandResult.Failure(
                        translatable("file.unsupported_extension", extension)
                    )
                }

                Logger.log(
                    LogLevel.INFO,
                    translatable("commands.compile.compiling", file.absolutePath)
                )
                val startCompileMillis = System.currentTimeMillis()

                val tokens = RuntimeFunctions.tokenize(file.readText())
                val program = RuntimeFunctions.parseTokens(file, tokens)
                val classes = RuntimeFunctions.compileProgram(program)

                val outputDirectory = File(getArgument(outputArg))
                if (!outputDirectory.exists()) {
                    if (!outputDirectory.mkdirs()) {
                        return@executes CommandResult.Failure(
                            translatable("file.cant_create", outputDirectory.absolutePath)
                        )
                    }
                }
                else outputDirectory.listFiles()?.forEach { it.delete() }

                for ((classDesc, classFile) in classes) {
                    val outputFile = File(outputDirectory, classDesc.displayName() + ".class")

                    try {
                        Files.write(outputFile.toPath(), classFile)
                    }
                    catch (e: IOException) {
                        throw RuntimeException(e)
                    }
                }

                val endCompileMillis = System.currentTimeMillis()
                Logger.log(
                    LogLevel.INFO,
                    translatable("commands.compile.info", (endCompileMillis - startCompileMillis).toDouble() / 1000)
                )

                Logger.log(LogLevel.INFO, translatable("commands.run.running", file.absolutePath))
                val startRunMillis = System.currentTimeMillis()
                RuntimeFunctions.loadClassesAndRun(classes)

                val endRunMillis = System.currentTimeMillis()
                return@executes CommandResult.Success(
                    translatable("commands.run.info", (endRunMillis - startRunMillis).toDouble() / 1000)
                )
            }
        }
    }
}