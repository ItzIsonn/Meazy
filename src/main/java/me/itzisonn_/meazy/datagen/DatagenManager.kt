package me.itzisonn_.meazy.datagen

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import me.itzisonn_.meazy.util.FileUtils
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.toPath
import kotlin.io.path.walk

/**
 * Provides methods for working with datagen
 */
class DatagenManager {
    /**
     * Gets all lines inside folder with given folderPath and deserializes them using given deserializer. Accepts only single value in JSON
     *
     * @param folderPath Path to datagen folder
     * @param cls Class of deserialized values
     * @param deserializer Json deserializer
     *
     * @return Set of all values inside folder with given folderPath
     * @param T Type of deserialized values
     */
    fun <T> getDeserializedSingle(folderPath: String, cls: Class<T>, deserializer: JsonDeserializer<T>): Set<T> {
        val result = mutableSetOf<T>()
        val gson = GsonBuilder().registerTypeAdapter(cls, deserializer).create()

        for (lines in getDatagenFilesLines(folderPath)) {
            val value = gson.fromJson(lines, cls)
            result.add(value)
        }

        return result
    }

    /**
     * Gets all lines inside folder with given folderPath and deserializes them using given deserializer. Accepts an array and single value in JSON
     *
     * @param folderPath Path to datagen folder
     * @param cls Class of deserialized values
     * @param deserializer Json deserializer
     *
     * @return Set of all values inside folder with given folderPath
     * @param T Type of deserialized values
     */
    fun <T> getDeserializedMultiple(folderPath: String, cls: Class<T>, deserializer: JsonDeserializer<T>): Set<T> {
        val result = mutableSetOf<T>()

        val gson = GsonBuilder().registerTypeAdapter(cls, deserializer).create()
        @Suppress("UNCHECKED_CAST")
        val typeToken = TypeToken.getParameterized(MutableSet::class.java, cls) as TypeToken<MutableSet<T>>

        for (lines in getDatagenFilesLines(folderPath)) {
            try {
                val value = gson.fromJson(lines, cls)
                result.add(value)
            }
            catch (_: JsonSyntaxException) {
                val values = gson.fromJson(lines, typeToken)
                result.addAll(values)
            }
        }

        return result
    }

    /**
     * @param folderPath Path to datagen folder
     * @return Set of all files' lines inside folder with given folderPath
     */
    fun getDatagenFilesLines(folderPath: String): Set<String> {
        val url = this::class.java.getResource("/data/$folderPath") ?: error("Can't find file: $folderPath")
        val path = url.toURI().toPath()

        try {
            return path.walk()
                .filter { path: Path -> Files.isRegularFile(path) }
                .map { obj: Path -> FileUtils.getLines(obj) }
                .toSet()
        }
        catch (e: IOException) {
            throw RuntimeException(e)
        }
        catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }
    }
}