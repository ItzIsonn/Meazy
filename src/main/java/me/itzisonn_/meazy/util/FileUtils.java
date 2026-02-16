package me.itzisonn_.meazy.util;

import org.jspecify.annotations.NullMarked;

import java.io.*;
import java.nio.file.Path;

/**
 * File utils
 */
@NullMarked
public final class FileUtils {
    private FileUtils() {}

    /**
     * @param file Target file
     * @return Extension of given file
     */
    public static String getExtension(File file) {
        String name = file.getName();

        int i = name.lastIndexOf('.');
        if (i > 0) return name.substring(i + 1);
        return "";
    }

    /**
     * @param file Target file
     * @return Name of given file without extension
     */
    public static String getNameWithoutExtension(File file) {
        String name = file.getName();

        int i = name.lastIndexOf('.');
        if (i > 0) return name.substring(0, i);
        return name;
    }

    /**
     * Returns lines of file at given path
     *
     * @param path Target path
     * @return Lines of file
     */
    public static String getLines(Path path) {
        try {
            return getLines(new FileInputStream(path.toFile()));
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("File doesn't exist", e);
        }
    }

    /**
     * Returns lines of given file
     *
     * @param file Target file
     * @return Lines of file
     */
    public static String getLines(File file) {
        try {
            return getLines(new FileInputStream(file));
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("File doesn't exist", e);
        }
    }

    /**
     * Returns lines of given inputStream
     *
     * @param inputStream Input stream
     * @return Lines of input stream
     */
    public static String getLines(InputStream inputStream) {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            return String.join("\n", inputStreamReader.readAllLines());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}