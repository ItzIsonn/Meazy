package me.itzisonn_.meazy.util

import org.jspecify.annotations.NullMarked
import java.io.*
import java.nio.file.Path
import kotlin.io.path.readLines

/**
 * File utils
 */
@NullMarked
object FileUtils {
    /**
     * @param file Target file
     * @return Extension of given file
     */
    @JvmStatic
    fun getExtension(file: File): String {
        return file.extension
    }

    /**
     * @param file Target file
     * @return Name of given file without extension
     */
    @JvmStatic
    fun getNameWithoutExtension(file: File): String {
        return file.nameWithoutExtension
    }

    /**
     * Returns lines of file at given path
     * 
     * @param path Target path
     * @return Lines of file
     */
    @JvmStatic
    fun getLines(path: Path): String {
        return path.readLines().joinToString("\n")
    }

    /**
     * Returns lines of given file
     * 
     * @param file Target file
     * @return Lines of file
     */
    @JvmStatic
    fun getLines(file: File): String {
        return file.readLines().joinToString("\n")
    }

    /**
     * Returns lines of given inputStream
     * 
     * @param inputStream Input stream
     * @return Lines of input stream
     */
    @JvmStatic
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