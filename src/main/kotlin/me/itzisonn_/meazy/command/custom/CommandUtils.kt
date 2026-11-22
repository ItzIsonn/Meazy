package me.itzisonn_.meazy.command.custom

import me.itzisonn_.meazy.command.CommandResult
import me.itzisonn_.meazy.parser.ast.statement.Program
import me.itzisonn_.meazy.runtime.ClassLoaderWrapper
import me.itzisonn_.meazy.runtime.RuntimeFunctions
import me.itzisonn_.meazy.util.Result
import me.itzisonn_.meazy.util.logger.LogLevel
import me.itzisonn_.meazy.util.logger.Logger
import me.itzisonn_.meazy.util.text.translatable
import java.io.File
import java.io.IOException
import java.lang.constant.ClassDesc
import java.nio.file.Files
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText
import kotlin.io.path.walk
import kotlin.reflect.KCallable
import kotlin.reflect.KVisibility
import kotlin.reflect.typeOf

object CommandUtils {
    fun compile(file: File): Result<Map<ClassDesc, ByteArray>, CommandResult> {
        if (file.isFile) {
            val extension = file.extension
            if (extension != "mea") {
                return Result.Failure(CommandResult.Failure(
                    translatable("file.unsupported_extension", extension)
                ))
            }

            Logger.log(
                LogLevel.INFO,
                translatable("commands.compile.compiling", file.absolutePath)
            )

            val tokens = RuntimeFunctions.tokenize(file.readText())
            val program = RuntimeFunctions.parseTokens(file.nameWithoutExtension, tokens)
            val classes = RuntimeFunctions.compilePrograms(listOf(program))

            return Result.Success(classes)
        }

        val paths = file.toPath().walk()
            .mapNotNull { path ->
                val extension = path.extension
                if (extension != "mea") null
                else path
            }
            .toList()

        if (paths.isEmpty()) {
            return Result.Failure(CommandResult.Failure(
                translatable("commands.compile.no_files", file.absolutePath)
            ))
        }

        Logger.log(
            LogLevel.INFO,
            translatable("commands.compile.compiling", file.absolutePath)
        )

        val programs = mutableListOf<Program>()

        paths.forEach { path ->
            val tokens = RuntimeFunctions.tokenize(path.readText())
            programs += RuntimeFunctions.parseTokens(path.nameWithoutExtension, tokens)
        }

        val classes = RuntimeFunctions.compilePrograms(programs)
        return Result.Success(classes)
    }



    fun outputClasses(outputDirectory: File, classes: Map<ClassDesc, ByteArray>): CommandResult {
        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                return CommandResult.Failure(
                    translatable("file.cant_create", outputDirectory.absolutePath)
                )
            }
        }
        else outputDirectory.listFiles()?.forEach { it.delete() }

        for ((classDesc, classFile) in classes) {
            var packageName = classDesc.packageName().replace(".", "/")
            if (packageName.isNotEmpty()) packageName += "/"

            val parentFile = outputDirectory.resolve(packageName)
            if (!parentFile.exists()) {
                if (!parentFile.mkdirs()) {
                    return CommandResult.Failure(
                        translatable("file.cant_create", parentFile.absolutePath)
                    )
                }
            }

            val outputFile = File(parentFile, classDesc.displayName() + ".class")

            try {
                Files.write(outputFile.toPath(), classFile)
            }
            catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        return CommandResult.Success()
    }



    fun loadClassesAndRun(classes: Map<ClassDesc, ByteArray>, absoluteFilePath: String): CommandResult {
        val classLoader = ClassLoaderWrapper()
        val mainFunctions = mutableSetOf<KCallable<*>>()

        for ((_, classFile) in classes) {
            val loadedClass = classLoader.defineClass(classFile).kotlin

            val function = loadedClass.members.find { it.name == "main" } ?: continue
            if (function.returnType != typeOf<Unit>() || function.parameters.isNotEmpty()) continue
            if (function.visibility != KVisibility.PUBLIC) continue

            mainFunctions += function
        }

        if (mainFunctions.isEmpty()) {
            return CommandResult.Failure(
                translatable("commands.run.no_main_function", absoluteFilePath)
            )
        }

        if (mainFunctions.size > 1) {
            return CommandResult.Failure(
                translatable("commands.run.multiple_main_functions", mainFunctions.size, absoluteFilePath)
            )
        }

        mainFunctions.first().call()
        return CommandResult.Success()
    }
}