package me.itzisonn_.meazy.version

import java.util.Objects
import java.util.regex.Pattern
import kotlin.math.max

/**
 * Represents version
 *
 * @param parts   Parts
 * @param type    Type
 * @param ordinal Ordinal
 *
 * @throws IllegalArgumentException If given parts is empty
 */
class Version(parts: List<Int>, type: VersionType, ordinal: Int) {
    val parts: List<Int>
    val type: VersionType
    val ordinal: Int

    /**
     * Checks if given version is before this version
     * 
     * @param version Version to check
     * @return Whether given version is before this version
     */
    fun isBefore(version: Version): Boolean {
        val parts1 = parts
        val parts2 = version.parts

        for (i in 0..<max(parts1.size, parts2.size)) {
            val part1 = if (i < parts1.size) parts1.get(i) else 0
            val part2 = if (i < parts2.size) parts2.get(i) else 0

            if (part1 < part2) return true
            if (part1 > part2) return false
        }

        return type.ordinal < version.type.ordinal || (type.ordinal == version.type.ordinal && ordinal < version.ordinal)
    }

    /**
     * Checks if given version is after this version
     * 
     * @param version Version to check
     * @return Whether given version is after this version
     */
    fun isAfter(version: Version): Boolean {
        return !equals(version) && !isBefore(version)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Version) return false

        val parts1 = parts
        val parts2 = other.parts

        for (i in 0..<max(parts1.size, parts2.size)) {
            val part1 = if (i < parts1.size) parts1[i] else 0
            val part2 = if (i < parts2.size) parts2[i] else 0

            if (part1 != part2) return false
        }

        return type.ordinal == other.type.ordinal && ordinal == other.ordinal
    }

    override fun hashCode(): Int {
        return Objects.hash(parts, type, ordinal)
    }

    override fun toString(): String {
        val partsString = parts.joinToString(".")

        val typeString: String
        val ordinalString: String
        if (type == VersionType.RELEASE && ordinal == 0) {
            typeString = ""
            ordinalString = ""
        }
        else {
            typeString = "-$type"
            ordinalString = ordinal.toString()
        }

        return partsString + typeString + ordinalString
    }


    init {
        require(!parts.isEmpty()) { "Parts can't be empty" }

        this.parts = parts.toList()
        this.type = type
        this.ordinal = ordinal
    }

    companion object {
        private val versionPattern: Pattern

        init {
            val possibleTypes = VersionType.VERSION_TYPES.keys.stream().sorted().toList().reversed().joinToString("|")
            versionPattern = Pattern.compile("(\\d+(\\.\\d+)*)(-($possibleTypes)(\\d*))?", Pattern.CASE_INSENSITIVE)
        }

        /**
         * Parses given string into [Version]
         * 
         * @param version String to parse
         * @return Parsed version
         * @throws IllegalArgumentException If given version is in invalid format
         */
        fun of(version: String): Version {
            val matcher = versionPattern.matcher(version)
            require(matcher.matches()) { "Invalid version '$version'" }

            val parts: List<Int>
            try {
                parts = matcher.group(1).split("\\.".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .map { s -> s.toInt() }
                    .toList()
            }
            catch (_: NumberFormatException) {
                throw IllegalArgumentException("Invalid version '$version'")
            }

            val versionType: VersionType?
            val versionTypeGroup = matcher.group(4)
            if (versionTypeGroup == null) versionType = VersionType.RELEASE
            else {
                versionType = VersionType.of(versionTypeGroup)
                requireNotNull(versionType) { "Invalid version '$version'" }
            }

            val ordinalGroup = matcher.group(5)
            val ordinal = if (ordinalGroup == null || ordinalGroup.isBlank()) 0
            else {
                try {
                    matcher.group(5).toInt()
                }
                catch (_: NumberFormatException) {
                    throw IllegalArgumentException("Invalid version '$version'")
                }
            }

            return Version(parts, versionType, ordinal)
        }
    }
}
