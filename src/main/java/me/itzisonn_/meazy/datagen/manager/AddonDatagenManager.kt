package me.itzisonn_.meazy.datagen.manager

import me.itzisonn_.meazy.util.FileUtils.getLines
import org.jspecify.annotations.NullMarked
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

/**
 * Provides methods for working with datagen TODO
 * @param file Addon's file
 */
@NullMarked
class AddonDatagenManager(private val file: File) : DatagenManager() {
    override fun getDatagenFilesLines(folderPath: String): MutableSet<String> {
        val result: MutableSet<String> = HashSet()

        try {
            ZipFile(file).use { zipFile ->
                val inputStream = ZipInputStream(FileInputStream(file))
                var zipEntry = inputStream.getNextEntry()
                while (zipEntry != null) {
                    if (!zipEntry.getName().startsWith("data/$folderPath/") || zipEntry.isDirectory) {
                        zipEntry = inputStream.getNextEntry()
                        continue
                    }

                    result.add(getLines(zipFile.getInputStream(zipEntry)))
                    zipEntry = inputStream.getNextEntry()
                }
                return result
            }
        }
        catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}
