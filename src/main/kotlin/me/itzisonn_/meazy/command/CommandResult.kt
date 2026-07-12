package me.itzisonn_.meazy.command

import me.itzisonn_.meazy.util.text.Text

sealed interface CommandResult {
    val text: Text?

    data class Success(override val text: Text? = null) : CommandResult
    data class Failure(override val text: Text? = null) : CommandResult
}