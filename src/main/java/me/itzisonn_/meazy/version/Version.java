package me.itzisonn_.meazy.version;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents version
 */
@Getter
@EqualsAndHashCode
public class Version {
    /**
     * Parts
     */
    private final List<Integer> parts;
    /**
     * Type
     */
    private final VersionType type;
    /**
     * Ordinal
     */
    private final int ordinal;

    /**
     * @param parts Parts
     * @param type Type
     * @param ordinal Ordinal
     *
     * @throws NullPointerException If either parts or type is null
     * @throws IllegalArgumentException If given parts is empty
     */
    public Version(List<Integer> parts, VersionType type, int ordinal) throws NullPointerException, IllegalArgumentException {
        if (parts == null) throw new NullPointerException("Parts can't be null");
        if (parts.isEmpty()) throw new IllegalArgumentException("Parts can't be empty");
        if (type == null) throw new NullPointerException("Type can't be null");

        this.parts = List.copyOf(parts);
        this.type = type;
        this.ordinal = ordinal;
    }

    /**
     * Checks if given version is before this version
     *
     * @param version Version to check
     * @return Whether given version is before this version
     */
    public boolean isBefore(Version version) {
        List<Integer> parts1 = parts;
        List<Integer> parts2 = version.getParts();

        for (int i = 0; i < Math.max(parts1.size(), parts2.size()); i++) {
            int part1 = i < parts1.size() ? parts1.get(i) : 0;
            int part2 = i < parts2.size() ? parts2.get(i) : 0;

            if (part1 < part2) return true;
            if (part1 > part2) return false;
        }

        if (type.ordinal() < version.getType().ordinal()) return true;
        if (type.ordinal() > version.getType().ordinal()) return false;

        return ordinal < version.getOrdinal();
    }

    /**
     * Checks if given version is after this version
     *
     * @param version Version to check
     * @return Whether given version is after this version
     */
    public boolean isAfter(Version version) {
        return !equals(version) && !isBefore(version);
    }

    @Override
    public String toString() {
        String partsString = String.join(".", parts.stream().map(String::valueOf).toList());

        String typeString;
        String ordinalString;
        if (type == VersionType.RELEASE && ordinal == 0) {
            typeString = "";
            ordinalString = "";
        }
        else {
            typeString = "-" + type.toString();
            ordinalString = String.valueOf(ordinal);
        }

        return partsString + typeString + ordinalString;
    }



    private static final Pattern versionPattern;

    static {
        String possibleTypes = String.join("|", VersionType.VERSION_TYPES.keySet().stream().sorted().toList().reversed());
        versionPattern =  Pattern.compile("(\\d+(\\.\\d+)*)(-(" + possibleTypes +")(\\d*))?", Pattern.CASE_INSENSITIVE);
    }

    /**
     * Parses given string into {@link Version}
     *
     * @param version String to parse
     * @return Parsed version
     *
     * @throws NullPointerException If given version is null
     * @throws IllegalArgumentException If given version is in invalid format
     */
    public static Version of(String version) throws NullPointerException, IllegalArgumentException {
        if (version == null) throw new NullPointerException("Version can't be null");

        Matcher matcher = versionPattern.matcher(version);
        if (!matcher.matches()) throw new IllegalArgumentException("Invalid version '" + version + "'");

        List<Integer> parts;
        try {
            parts = Arrays.stream(matcher.group(1).split("\\.")).map(Integer::parseInt).toList();
        }
        catch (NumberFormatException ignore) {
            throw new IllegalArgumentException("Invalid version '" + version + "'");
        }

        VersionType versionType;
        String versionTypeGroup = matcher.group(4);
        if (versionTypeGroup == null) versionType = VersionType.RELEASE;
        else {
            versionType = VersionType.VERSION_TYPES.get(versionTypeGroup.toUpperCase());
            if (versionType == null) throw new IllegalArgumentException("Invalid version '" + version + "'");
        }

        int ordinal;
        String ordinalGroup = matcher.group(5);
        if (ordinalGroup == null || ordinalGroup.isBlank()) ordinal = 0;
        else {
            try {
                ordinal = Integer.parseInt(matcher.group(5));
            }
            catch (NumberFormatException ignore) {
                throw new IllegalArgumentException("Invalid version '" + version + "'");
            }
        }

        return new Version(parts, versionType, ordinal);
    }
}
