package me.itzisonn_.meazy.version;

import java.util.Map;

public enum VersionType {
    ALPHA,
    BETA,
    RELEASE_CANDIDATE,
    RELEASE;

    public static VersionType of(String version) {
        return VERSION_TYPES.get(version.toUpperCase());
    }

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
