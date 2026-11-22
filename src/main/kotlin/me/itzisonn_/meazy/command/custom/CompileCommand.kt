package me.itzisonn_.meazy.command.custom

import me.itzisonn_.meazy.command.Command
import me.itzisonn_.meazy.command.CommandResult
import me.itzisonn_.meazy.command.StringType
import me.itzisonn_.meazy.util.Result
import me.itzisonn_.meazy.util.text.translatable
import java.io.File
import java.lang.constant.ClassDesc
import kotlin.system.measureTimeMillis

val compileCommand = Command("compile") {
    argument("target", StringType) { targetArg ->
        argument("output_directory", StringType) { outputArg ->
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

                        val outputtingTime = measureTimeMillis {
                            val outputDirectory = File(getArgument(outputArg))
                            val outputResult = CommandUtils.outputClasses(outputDirectory, classes)
                            if (outputResult is CommandResult.Failure) return@executes outputResult
                        }

                        return@executes CommandResult.Success(
                            translatable("commands.compile.info", (compilingTime + outputtingTime) / 1000.0)
                        )
                    }
                }
            }
        }
    }
}