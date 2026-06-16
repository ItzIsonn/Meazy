package me.itzisonn_.meazy.command.custom

import me.itzisonn_.meazy.command.Command
import me.itzisonn_.meazy.command.CommandResult
import me.itzisonn_.meazy.command.StringType
import me.itzisonn_.meazy.text.translatable
import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.meazy.logger.LogLevel
import me.itzisonn_.meazy.logger.Logger
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
                        translatable("meazy:file.doesnt_exist", file.absolutePath)
                    )
                }

                val extension = file.extension
                if (extension != "mea") {
                    return@executes CommandResult.Failure(
                        translatable("meazy:file.unsupported_extension", extension)
                    )
                }

                Logger.log(
                    LogLevel.INFO,
                    translatable("meazy:commands.compile.compiling", file.absolutePath)
                )
                val startCompileMillis = System.currentTimeMillis()

                val tokens = Registries.tokenizationFunction(file.readText())
                val program = Registries.parseTokensFunction(file, tokens)
                val classes = Registries.compileProgramFunction(program)

                val outputDirectory = File(getArgument(outputArg))
                if (!outputDirectory.exists()) {
                    if (!outputDirectory.mkdirs()) {
                        return@executes CommandResult.Failure(
                            translatable("meazy:file.cant_create", outputDirectory.absolutePath)
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
                    translatable("meazy:commands.compile.info", (endCompileMillis - startCompileMillis).toDouble() / 1000)
                )

                Logger.log(LogLevel.INFO, translatable("meazy:commands.run.running", file.absolutePath))
                val startRunMillis = System.currentTimeMillis()
                Registries.runProgramFunction(classes)

                val endRunMillis = System.currentTimeMillis()
                return@executes CommandResult.Success(
                    translatable("meazy:commands.run.info", (endRunMillis - startRunMillis).toDouble() / 1000)
                )
            }
        }
    }
}