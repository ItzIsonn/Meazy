package me.itzisonn_.meazy.command.custom

import me.itzisonn_.meazy.command.Command
import me.itzisonn_.meazy.command.CommandResult
import me.itzisonn_.meazy.command.StringType
import me.itzisonn_.meazy.runtime.RuntimeFunctions
import me.itzisonn_.meazy.util.text.translatable
import me.itzisonn_.meazy.util.logger.LogLevel
import me.itzisonn_.meazy.util.logger.Logger
import java.io.File
import java.lang.constant.ClassDesc
import kotlin.system.measureTimeMillis

val runCommand = Command("run") {
    argument("target_file", StringType) { targetArg ->
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

            val classes: Map<ClassDesc, ByteArray>

            val compilingTime = measureTimeMillis {
                val tokens = RuntimeFunctions.tokenize(file.readText())
                val program = RuntimeFunctions.parseTokens(file, tokens)
                classes = RuntimeFunctions.compileProgram(program)
            }

            Logger.log(
                LogLevel.INFO,
                translatable("commands.compile.info", compilingTime / 1000.0)
            )

            Logger.log(LogLevel.INFO, translatable("commands.run.running", file.absolutePath))

            val runningTime = measureTimeMillis {
                RuntimeFunctions.loadClassesAndRun(classes)
            }

            return@executes CommandResult.Success(
                translatable("commands.run.info", runningTime / 1000.0)
            )
        }
    }
}