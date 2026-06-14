package me.itzisonn_.meazy.util

import java.io.*
import java.nio.file.Path
import kotlin.io.path.readLines

/**
 * File utils
 */
object FileUtils {
    /**
     * Returns lines of file at given path
     * 
     * @param path Target path
     * @return Lines of file
     */
    fun getLines(path: Path): String {
        return path.readLines().joinToString("\n")
    }

    /**
     * Returns lines of given file
     * 
     * @param file Target file
     * @return Lines of file
     */
    fun getLines(file: File): String {
        return file.readLines().joinToString("\n")
    }

    /**
     * Returns lines of given inputStream
     * 
     * @param inputStream Input stream
     * @return Lines of input stream
     */
    fun getLines(inputStream: InputStream): String {
        try {
            InputStreamReader(inputStream).use { inputStreamReader ->
                return inputStreamReader.readAllLines().joinToString("\n")
            }
        }
        catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}