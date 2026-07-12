package me.itzisonn_.meazy.util.datagen

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import kotlin.io.path.readText
import kotlin.io.path.toPath
import kotlin.io.path.walk

/**
 * Provides methods for working with datagen
 */
object DatagenManager {
    /**
     * Gets content of all files inside folder with given folderPath and deserializes them using given deserializer. Accepts only single value in JSON
     *
     * @param folderPath Path to datagen folder
     * @param deserializer Deserializer
     * @param T Type of deserialized values
     *
     * @return Set of deserialized values
     */
    fun <T> getDeserializedSingle(folderPath: String, deserializer: DeserializationStrategy<T>): Set<T> {
        val result = mutableSetOf<T>()

        for (content in getDatagenFilesContent(folderPath)) {
            val value = Json.decodeFromString(deserializer, content)
            result += value
        }

        return result
    }

    /**
     * Gets content of all files inside folder with given folderPath and deserializes them using given deserializer. Accepts an array and single value in JSON
     *
     * @param folderPath Path to datagen folder
     * @param deserializer Deserializer
     * @param T Type of deserialized values
     *
     * @return Set of deserialized values
     */
    fun <T : Any> getDeserializedMultiple(folderPath: String, deserializer: KSerializer<T>): Set<T> {
        val result = mutableSetOf<T>()

        for (content in getDatagenFilesContent(folderPath)) {
            try {
                val value = Json.decodeFromString(deserializer, content)
                result.add(value)
            }
            catch (_: SerializationException) {
                val values = Json.decodeFromString(SetSerializer(deserializer), content)
                result.addAll(values)
            }
        }

        return result
    }



    private fun getDatagenFilesContent(folderPath: String): Set<String> {
        val url = this::class.java.getResource("/data/$folderPath") ?: error("Can't find file: $folderPath")
        val path = url.toURI().toPath()

        try {
            return path.walk()
                .filter { path -> Files.isRegularFile(path) }
                .map { path -> path.readText() }
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