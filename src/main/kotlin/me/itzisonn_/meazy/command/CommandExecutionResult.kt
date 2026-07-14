package me.itzisonn_.meazy.command

sealed interface CommandExecutionResult {
    object NoArgs : CommandExecutionResult
    object UnknownCommand : CommandExecutionResult
    data class Result(val result: CommandResult) : CommandExecutionResult
}