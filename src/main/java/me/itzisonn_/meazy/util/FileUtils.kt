package me.itzisonn_.meazy.util

import java.io.File

/**
 * File utils
 */
object FileUtils {
    /**
     * Returns lines of given file
     * 
     * @param file Target file
     * @return Lines of file
     */
    fun getLines(file: File): String {
        return file.readLines().joinToString("\n")
    }
}