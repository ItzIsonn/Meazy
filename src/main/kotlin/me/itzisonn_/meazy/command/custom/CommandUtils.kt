package me.itzisonn_.meazy.command.custom

import me.itzisonn_.meazy.command.CommandResult
import me.itzisonn_.meazy.util.text.translatable
import java.io.File
import java.io.IOException
import java.lang.constant.ClassDesc
import java.nio.file.Files

object CommandUtils {
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
}