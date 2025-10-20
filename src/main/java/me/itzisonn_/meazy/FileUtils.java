package me.itzisonn_.meazy;

import java.io.*;

/**
 * File utils
 */
public final class FileUtils {
    private FileUtils() {}

    /**
     * Returns extension of given file
     *
     * @param file Target file
     * @return Extension of file
     *
     * @throws NullPointerException If given file is null
     */
    public static String getExtension(File file) throws NullPointerException {
        if (file == null) throw new NullPointerException("File can't be null");
        String name = file.getName();

        int i = name.lastIndexOf('.');
        if (i > 0) {
            return name.substring(i + 1);
        }
        return "";
    }

    /**
     * Returns lines of given file
     *
     * @param file Target file
     * @return Lines of file
     *
     * @throws NullPointerException If given file is null
     */
    public static String getLines(File file) throws NullPointerException {
        if (file == null) throw new NullPointerException("File can't be null");

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
     *
     * @throws NullPointerException If given inputStream is null
     */
    public static String getLines(InputStream inputStream) throws NullPointerException {
        if (inputStream == null) throw new NullPointerException("InputStream can't be null");

        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = reader.readLine();

            while (line != null) {
                stringBuilder.append(line.trim()).append("\n");
                line = reader.readLine();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stringBuilder.toString();
    }
}