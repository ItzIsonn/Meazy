package me.itzisonn_.meazy.command.custom

import me.itzisonn_.meazy.command.Command
import me.itzisonn_.meazy.command.CommandResult
import me.itzisonn_.meazy.command.StringType
import me.itzisonn_.meazy.util.Result
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
            if (!file.exists()) {
                return@executes CommandResult.Failure(
                    translatable("file.doesnt_exist", file.absolutePath)
                )
            }

            val compileResult: Result<Map<ClassDesc, ByteArray>, CommandResult>
            val compilingTime = measureTimeMillis {
                compileResult = CommandUtils.compile(file)
            }

            when (compileResult) {
                is Result.Failure -> return@executes compileResult.error
                is Result.Success -> {
                    val classes = compileResult.value

                    Logger.log(
                        LogLevel.INFO,
                        translatable("commands.compile.info", compilingTime / 1000.0)
                    )

                    Logger.log(LogLevel.INFO, translatable("commands.run.running", file.absolutePath))

                    val runningTime = measureTimeMillis {
                        val runResult = CommandUtils.loadClassesAndRun(classes, file.absolutePath)
                        if (runResult is CommandResult.Failure) return@executes runResult
                    }

                    return@executes CommandResult.Success(
                        translatable("commands.run.info", runningTime / 1000.0)
                    )
                }
            }
        }
    }
}