package me.itzisonn_.meazy;

import me.itzisonn_.meazy.runtime.value.RuntimeValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
     * Counts number of target's matches in given string
     *
     * @param string The string to count in
     * @param target The target to match
     * @return Number of target's matches in given string
     *
     * @throws NullPointerException When either of strings is null
     */
    public static int countMatches(String string, String target) throws NullPointerException {
        if (string == null || target == null) throw new NullPointerException("Neither of strings can't be null!");
        return (string.length() - string.replace(target, "").length()) / target.length();
    }

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

    /**
     * Returns offset represented by a string
     *
     * @param offset Number of offsets
     * @return String offset
     *
     * @throws IllegalArgumentException When given offset is negative
     */
    public static String getOffset(int offset) throws IllegalArgumentException {
        if (offset < 0) throw new IllegalArgumentException("Offset can't be negative");

        return "\t".repeat(offset);
    }

    /**
     * Iterates over given collection and uses {@link RuntimeValue#getFinalValue()} function on all elements.
     *
     * @param collection Collection of RuntimeValues
     * @return Unpacked list
     */
    public static List<Object> unpackRuntimeValuesCollection(Collection<RuntimeValue<?>> collection) {
        List<Object> unpackedList = new ArrayList<>();
        for (RuntimeValue<?> runtimeValue : collection) {
            unpackedList.add(runtimeValue.getFinalValue());
        }
        return unpackedList;
    }

    /**
     * Generates name with prefix:<br>
     * - If given name is uppercase, returns value in format PREFIX_NAME<br>
     * - Else returns value in format prefixName
     *
     * @param prefix Prefix
     * @param name Name
     * @return Generated name
     */
    public static String generatePrefixedName(String prefix, String name) {
        if (name.equals(name.toUpperCase())) return prefix.toUpperCase() + "_" + name;
        return prefix + name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}