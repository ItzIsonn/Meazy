package me.itzisonn_.meazy.command.custom

import me.itzisonn_.meazy.command.Command
import me.itzisonn_.meazy.command.CommandResult
import me.itzisonn_.meazy.command.StringType
import me.itzisonn_.meazy.text.translatable
import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.meazy.logger.LogLevel
import me.itzisonn_.meazy.logger.Logger
import java.io.File

val runCommand = Command("run") {
    argument("target_file", StringType) { targetArg ->
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

            Logger.log(LogLevel.INFO, translatable("meazy:commands.run.running", file.absolutePath))
            val startMillis = System.currentTimeMillis()

            val tokens = Registries.tokenizationFunction(file.readText())
            val program = Registries.parseTokensFunction(file, tokens)

            val classes = Registries.compileProgramFunction(program)
            Registries.runProgramFunction(classes)

            val endMillis = System.currentTimeMillis()
            return@executes CommandResult.Success(
                translatable("meazy:commands.run.info", (endMillis - startMillis).toDouble() / 1000)
            )
        }
    }
}