package me.itzisonn_.meazy.datagen.manager

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import org.jspecify.annotations.NullMarked

/**
 * Provides methods for working with datagen
 */
@NullMarked
abstract class DatagenManager {
    /**
     * Gets all lines inside folder with given folderPath and deserializes them using given deserializer. Accepts only single value in JSON
     * 
     * @param folderPath Path to datagen folder
     * @param cls Class of deserialized values
     * @param deserializer Json deserializer
     * 
     * @return Set of all values inside folder with given folderPath
     * @param <T> Type of deserialized values
    </T> */
    fun <T> getDeserializedSingle(
        folderPath: String,
        cls: Class<T?>,
        deserializer: JsonDeserializer<T?>
    ): MutableSet<T> {
        val result: MutableSet<T> = HashSet()
        val gson = GsonBuilder().registerTypeAdapter(cls, deserializer).create()

        for (lines in getDatagenFilesLines(folderPath)!!) {
            val value = gson.fromJson<T>(lines, cls)
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
     * @param <T> Type of deserialized values
    </T> */
    fun <T> getDeserializedMultiple(
        folderPath: String,
        cls: Class<T?>,
        deserializer: JsonDeserializer<T?>
    ): MutableSet<T> {
        val result: MutableSet<T> = HashSet()

        val gson = GsonBuilder().registerTypeAdapter(cls, deserializer).create()
        val typeToken = TypeToken.getParameterized(MutableSet::class.java, cls) as TypeToken<MutableSet<T>>

        for (lines in getDatagenFilesLines(folderPath)!!) {
            try {
                val value = gson.fromJson<T>(lines, cls)
                result.add(value)
            }
            catch (e: JsonSyntaxException) {
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
    abstract fun getDatagenFilesLines(folderPath: String): MutableSet<String>
}
