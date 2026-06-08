package me.itzisonn_.meazy.datagen.manager

import me.itzisonn_.meazy.util.FileUtils
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

/**
 * TODO
 */
class MeazyDatagenManager : DatagenManager() {
    override fun getDatagenFilesLines(folderPath: String): MutableSet<String> {
        val url = MeazyDatagenManager::class.java.getResource("/data/$folderPath")
            ?: error("Can't find file: $folderPath")

        try {
            Files.walk(Path.of(url.toURI())).use { paths ->
                return paths
                    .filter { path: Path -> Files.isRegularFile(path) }
                    .map { obj: Path -> FileUtils.getLines(obj) }
                    .collect(Collectors.toSet())
            }
        }
        catch (e: IOException) {
            throw RuntimeException(e)
        }
        catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
    }
}
