package me.itzisonn_.meazy.helper

import me.itzisonn_.meazy.Meazy
import me.itzisonn_.meazy.command.custom.CommandUtils
import me.itzisonn_.meazy.runtime.RuntimeFunctions
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.toPath

object TestingHelper {
    fun initialize() {
        if (!Meazy.isInitialized) {
            Meazy.initialize()
        }
    }

    fun run(name: String, contents: String) {
        val tokens = RuntimeFunctions.tokenize(contents)
        val program = RuntimeFunctions.parseTokens(name, tokens)

        val classes = RuntimeFunctions.compilePrograms(listOf(program))
        CommandUtils.loadClassesAndRun(classes, name)
    }

    fun loadAndRun(path: String) = OutputInterceptor.capture {
        val resource = this::class.java.getResource("/test/$path.mea") ?: error("Can't load test with path '$path'")
        val fullPath = resource.toURI().toPath().nameWithoutExtension
        val contents = resource.readText()
        run(fullPath, contents)
    }.replace(System.lineSeparator(), "\n")
}

fun asMultiline(vararg values: Any) = values.joinToString("\n", postfix = "\n")