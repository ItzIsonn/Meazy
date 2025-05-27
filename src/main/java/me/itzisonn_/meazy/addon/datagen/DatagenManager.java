package me.itzisonn_.meazy.addon.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import me.itzisonn_.meazy.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Provides methods for working with datagen
 */
public class DatagenManager {
    /**
     * Addon's file
     */
    private final File file;

    /**
     * @param file Addon's file
     * @throws NullPointerException If given file is null
     */
    public DatagenManager(File file) throws NullPointerException {
        if (file == null) throw new NullPointerException("File can't be null");
        this.file = file;
    }

    /**
     * Gets all lines inside folder with given folderPath and deserializes them using given deserializer. Accepts only single value in json
     *
     * @param folderPath Path to datagen folder
     * @param cls Class of deserialized values
     * @param deserializer Json deserializer
     *
     * @return Set of all values inside folder with given folderPath
     * @param <T> Type of deserialized values
     *
     * @throws NullPointerException If either folderPath, cls or deserializer is null
     */
    public <T> Set<T> getDeserializedSingle(String folderPath, Class<T> cls, JsonDeserializer<T> deserializer) throws NullPointerException {
        if (cls == null) throw new NullPointerException("Class can't be null");
        if (deserializer == null) throw new NullPointerException("Deserializer can't be null");

        Set<T> result = new HashSet<>();
        Gson gson = new GsonBuilder().registerTypeAdapter(cls, deserializer).create();

        for (String lines : getDatagenFilesLines(folderPath)) {
            T value = gson.fromJson(lines, cls);
            result.add(value);
        }

        return result;
    }

    /**
     * Gets all lines inside folder with given folderPath and deserializes them using given deserializer. Accepts array and single value in json
     *
     * @param folderPath Path to datagen folder
     * @param cls Class of deserialized values
     * @param deserializer Json deserializer
     *
     * @return Set of all values inside folder with given folderPath
     * @param <T> Type of deserialized values
     *
     * @throws NullPointerException If either folderPath, cls or deserializer is null
     */
    @SuppressWarnings("unchecked")
    public <T> Set<T> getDeserializedMultiple(String folderPath, Class<T> cls, JsonDeserializer<T> deserializer) throws NullPointerException {
        if (cls == null) throw new NullPointerException("Class can't be null");
        if (deserializer == null) throw new NullPointerException("Deserializer can't be null");

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
     *
     * @throws NullPointerException If given folderPath is null
     */
    public Set<String> getDatagenFilesLines(String folderPath) throws NullPointerException {
        return new HashSet<>(getDatagenInputStreams(folderPath, (FileUtils::getLines)));
    }

    private <T> Set<T> getDatagenInputStreams(String folderPath, Function<InputStream, T> converter) throws NullPointerException {
        if (folderPath == null) throw new NullPointerException("FolderPath can't be null");
        if (converter == null) throw new NullPointerException("Converter can't be null");

        Set<T> result = new HashSet<>();

        try (ZipFile zipFile = new ZipFile(file)) {
            ZipInputStream inputStream = new ZipInputStream(new FileInputStream(file));

            ZipEntry zipEntry = inputStream.getNextEntry();
            while (zipEntry != null) {
                if (!zipEntry.getName().startsWith("data/" + folderPath + "/") || zipEntry.isDirectory()) {
                    zipEntry = inputStream.getNextEntry();
                    continue;
                }

                result.add(converter.apply(zipFile.getInputStream(zipEntry)));
                zipEntry = inputStream.getNextEntry();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
