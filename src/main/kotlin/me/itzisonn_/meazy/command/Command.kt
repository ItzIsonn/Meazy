package me.itzisonn_.meazy.command

sealed interface Command {
    val id: String
    val arguments: List<Argument>

    fun execute(args: List<String>): CommandResult
}



sealed interface Argument {
    val id: String
    val children: List<Argument>
    val execute: (CommandContext.() -> CommandResult)?
}

sealed interface LiteralArgument : Argument

sealed interface TypedArgument<T> : Argument {
    val type: ArgumentType<T>
}



sealed interface CommandContext {
    fun <T> getArgument(argument: TypedArgument<T>): T
}



sealed interface ArgumentBuilder {
    fun literal(id: String, block: ArgumentBuilder.() -> Unit)
    fun <T> argument(id: String, type: ArgumentType<T>, block: ArgumentBuilder.(TypedArgument<T>) -> Unit)
    fun executes(execute: CommandContext.() -> CommandResult)
}