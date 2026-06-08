package me.itzisonn_.meazy.datagen.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides methods for working with datagen
 */
@NullMarked
public abstract class DatagenManager {
    /**
     * Gets all lines inside folder with given folderPath and deserializes them using given deserializer. Accepts only single value in JSON
     *
     * @param folderPath Path to datagen folder
     * @param cls Class of deserialized values
     * @param deserializer Json deserializer
     *
     * @return Set of all values inside folder with given folderPath
     * @param <T> Type of deserialized values
     */
    public <T> Set<T> getDeserializedSingle(String folderPath, Class<T> cls, JsonDeserializer<T> deserializer) {
        Set<T> result = new HashSet<>();
        Gson gson = new GsonBuilder().registerTypeAdapter(cls, deserializer).create();

        for (String lines : getDatagenFilesLines(folderPath)) {
            T value = gson.fromJson(lines, cls);
            result.add(value);
        }

        return result;
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
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> getDeserializedMultiple(String folderPath, Class<T> cls, JsonDeserializer<T> deserializer) {
        Set<T> result = new HashSet<>();

        Gson gson = new GsonBuilder().registerTypeAdapter(cls, deserializer).create();
        TypeToken<Set<T>> typeToken = (TypeToken<Set<T>>) TypeToken.getParameterized(Set.class, cls);

        for (String lines : getDatagenFilesLines(folderPath)) {
            try {
                T value = gson.fromJson(lines, cls);
                result.add(value);
            }
            catch (JsonSyntaxException e) {
                Set<T> values = gson.fromJson(lines, typeToken);
                result.addAll(values);
            }
        }

        return result;
    }

    /**
     * @param folderPath Path to datagen folder
     * @return Set of all files' lines inside folder with given folderPath
     */
    public abstract Set<String> getDatagenFilesLines(String folderPath);
}
