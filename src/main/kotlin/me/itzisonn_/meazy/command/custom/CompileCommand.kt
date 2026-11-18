package me.itzisonn_.meazy.command.custom

import me.itzisonn_.meazy.command.Command
import me.itzisonn_.meazy.command.CommandResult
import me.itzisonn_.meazy.command.StringType
import me.itzisonn_.meazy.runtime.RuntimeFunctions
import me.itzisonn_.meazy.util.text.translatable
import me.itzisonn_.meazy.util.logger.LogLevel
import me.itzisonn_.meazy.util.logger.Logger
import java.io.File
import kotlin.system.measureTimeMillis

val compileCommand = Command("compile") {
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

                val compilingTime = measureTimeMillis {
                    val tokens = RuntimeFunctions.tokenize(file.readText())
                    val program = RuntimeFunctions.parseTokens(file.nameWithoutExtension, tokens)
                    val classes = RuntimeFunctions.compileProgram(program)

                    val outputDirectory = File(getArgument(outputArg))
                    val result = CommandUtils.outputClasses(outputDirectory, classes)
                    if (result is CommandResult.Failure) return@executes result
                }

                CommandResult.Success(
                    translatable("commands.compile.info", compilingTime / 1000.0)
                )
            }
        }
    }
}