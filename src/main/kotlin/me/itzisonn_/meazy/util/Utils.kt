package me.itzisonn_.meazy.util

inline fun <T> tryOrNull(block: () -> T): T? {
    return try {
        block()
    }
    catch (_: Exception) {
        null
    }
}