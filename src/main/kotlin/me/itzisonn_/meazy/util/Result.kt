package me.itzisonn_.meazy.util

sealed interface Result<out T, out E> {
    data class Success<T, out E>(val value: T) : Result<T, E>
    data class Failure<out T, E>(val error: E) : Result<T, E>
}