package me.itzisonn_.meazy.util

import kotlin.time.Duration

inline fun <T> tryOrNull(block: () -> T): T? {
    return try {
        block()
    }
    catch (_: Exception) {
        null
    }
}



/**
 * This function exists because [Duration.toString] returns string with 6 millis chars which is too much
 */
fun Duration.toStringWithAdequateMillisRepresentation(): String = when {
    !isNegative() && !isPositive() -> "0s"
    isInfinite() && isPositive() -> "Infinity"
    isInfinite() && isNegative() -> "-Infinity"
    else -> {
        val isNegative = isNegative()

        buildString {
            if (isNegative) append('-')

            absoluteValue.toComponents { days, hours, minutes, seconds, nanoseconds ->
                val hasDays = days != 0L
                val hasHours = hours != 0
                val hasMinutes = minutes != 0
                val hasSeconds = seconds != 0
                val hasNanoseconds = nanoseconds != 0 && !hasMinutes && !hasHours && !hasDays
                var components = 0

                if (hasDays) {
                    append(days).append('d')
                    components++
                }

                if (hasHours || (hasDays && (hasMinutes || hasSeconds))) {
                    if (components++ > 0) append(' ')
                    append(hours).append('h')
                }

                if (hasMinutes || (hasSeconds && (hasHours || hasDays))) {
                    if (components++ > 0) append(' ')
                    append(minutes).append('m')
                }

                if (hasSeconds || hasNanoseconds) {
                    if (components++ > 0) append(' ')
                    append(seconds)

                    if (hasNanoseconds) {
                        append('.')
                        val fracString = nanoseconds.toString().padStart(3, '0').take(3)
                        appendRange(fracString, 0, fracString.indexOfLast { it != '0' } + 1)
                    }

                    append("s")
                }

                if (isNegative && components > 1) insert(1, '(').append(')')
            }
        }
    }
}