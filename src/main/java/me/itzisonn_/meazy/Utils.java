package me.itzisonn_.meazy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Utility class
 */
public final class Utils {
    private Utils() {}

    /**
     * Regex used by all identifiers
     */
    public static final String IDENTIFIER_REGEX = "[a-zA-Z_][a-zA-Z0-9_]*";

    /**
     * Returns extension of given file
     *
     * @param file Target file
     * @return Extension of file
     *
     * @throws NullPointerException When given file is null
     */
    public static String getExtension(File file) throws NullPointerException {
        if (file == null) throw new NullPointerException("File can't be null!");
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
     * @throws NullPointerException When given file is null
     */
    public static String getLines(File file) throws NullPointerException {
        if (file == null) throw new NullPointerException("File can't be null!");

        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
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

    /**
     * Compares to versions in format 'a.b.c...'
     *
     * @param version1 Version to compare
     * @param version2 Version to compare
     * @return True if version2 was released after version1, otherwise false
     *
     * @throws NullPointerException When either of versions is null
     * @throws NumberFormatException When either of version have non-integer part
     */
    public static boolean isVersionAfter(String version1, String version2) throws NullPointerException, NumberFormatException {
        if (version1 == null || version2 == null) throw new NullPointerException("Neither of versions can't be null");

        String[] split1 = version1.split("\\.");
        String[] split2 = version2.split("\\.");
        for (int i = 0; i < Math.max(split1.length, split2.length); i++) {
            int part1, part2;
            try {
                part1 = i < split1.length ? Integer.parseInt(split1[i]) : 0;
                part2 = i < split2.length ? Integer.parseInt(split2[i]) : 0;
            }
            catch (NumberFormatException ignore) {
                throw new IllegalArgumentException("Version parts must be integers");
            }
            if (part1 < part2) return true;
            if (part1 > part2) return false;
        }
        return false;
    }
}