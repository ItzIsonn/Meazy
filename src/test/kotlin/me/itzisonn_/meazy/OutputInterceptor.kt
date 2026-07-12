package me.itzisonn_.meazy

import java.io.ByteArrayOutputStream
import java.io.PrintStream

object OutputInterceptor {
    private val originalOut = System.out

    fun capture(block: () -> Unit): String {
        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream)

        try {
            System.setOut(printStream)
            block()
        }
        finally {
            System.setOut(originalOut)
            printStream.close()
        }

        return outputStream.toString(Charsets.UTF_8)
    }
}