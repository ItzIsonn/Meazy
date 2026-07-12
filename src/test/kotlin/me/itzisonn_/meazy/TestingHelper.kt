package me.itzisonn_.meazy

import me.itzisonn_.meazy.runtime.RuntimeFunctions

object TestingHelper {
    fun run(string: String) {
        val tokens = RuntimeFunctions.tokenize(string)
        val program = RuntimeFunctions.parseTokens(null, tokens)

        val classes = RuntimeFunctions.compileProgram(program)
        RuntimeFunctions.loadClassesAndRun(classes)
    }

    fun loadTest(path: String): String {
        return this::class.java.getResourceAsStream("/test/$path.mea")
            ?.bufferedReader()
            ?.use { it.readText() }
            ?: error("Can't load test with path '$path'")
    }

    fun loadAndRun(path: String) = OutputInterceptor.capture {
        val string = loadTest(path)
        run(string)
    }.replace(System.lineSeparator(), "\n")
}