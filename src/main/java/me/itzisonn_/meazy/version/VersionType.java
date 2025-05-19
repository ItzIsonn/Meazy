package me.itzisonn_.meazy.version;

import java.util.Map;

/**
 * Represents different types of version
 */
public enum VersionType {
    /**
     * Alpha version type
     */
    ALPHA,
    /**
     * Beta version type
     */
    BETA,
    /**
     * Release candidate version type
     */
    RELEASE_CANDIDATE,
    /**
     * Release version type
     */
    RELEASE;

    /**
     * @param string String representation of {@link VersionType}
     * @return Version type that matches given string or null
     */
    public static VersionType of(String string) {
        return VERSION_TYPES.get(string.toUpperCase());
    }

    /**
     * Map of different spellings of version types
     */
    public static final Map<String, VersionType> VERSION_TYPES = Map.of(
            "ALPHA", ALPHA,
            "A", ALPHA,
            "BETA", BETA,
            "B", BETA,
            "RELEASE_CANDIDATE", RELEASE_CANDIDATE,
            "RELEASE-CANDIDATE", RELEASE_CANDIDATE,
            "RC", RELEASE_CANDIDATE,
            "RELEASE", RELEASE,
            "R", RELEASE
    );
}
