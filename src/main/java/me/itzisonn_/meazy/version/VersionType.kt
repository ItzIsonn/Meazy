package me.itzisonn_.meazy.version

import java.util.*

/**
 * Represents different types of version
 */
enum class VersionType {
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

    companion object {
        /**
         * @param string String representation of [VersionType]
         * @return Version type that matches given string or null
         */
        fun of(string: String): VersionType? {
            return VERSION_TYPES[string.uppercase(Locale.getDefault())]
        }

        /**
         * Map of different spellings of version types
         */
        val VERSION_TYPES = mapOf(
            "ALPHA" to ALPHA,
            "A" to ALPHA,
            "BETA" to BETA,
            "B" to BETA,
            "RELEASE_CANDIDATE" to RELEASE_CANDIDATE,
            "RELEASE-CANDIDATE" to RELEASE_CANDIDATE,
            "RC" to RELEASE_CANDIDATE,
            "RELEASE" to RELEASE,
            "R" to RELEASE
        )
    }
}
