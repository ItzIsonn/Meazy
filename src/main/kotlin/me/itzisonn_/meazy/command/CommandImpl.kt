package me.itzisonn_.meazy.command

import me.itzisonn_.meazy.util.text.translatable

private class CommandImpl(
    override val id: String,
    block: ArgumentBuilder.() -> Unit
) : Command {
    private val argumentBuilder = ArgumentBuilderImpl().apply { block() }
    override val arguments = argumentBuilder.arguments

    override fun execute(args: List<String>): CommandResult {
        val map = mutableMapOf<TypedArgument<*>, String>()
        var arguments = arguments
        var execute: ((CommandContext) -> CommandResult)? = null

        args.forEachIndexed { i, arg ->
            if (arguments.isEmpty()) {
                return CommandResult.Failure(
                    translatable("commands.incorrect_args_amount", id)
                )
            }

            arguments
                .filterIsInstance<LiteralArgument>()
                .find { it.id == arg }
                ?.let { argument ->
                    arguments = argument.children
                    execute = argument.execute
                    return@forEachIndexed
                }

            arguments
                .filterIsInstance<TypedArgument<*>>()
                .find { it.type.matches(arg) }
                ?.let { argument ->
                    arguments = argument.children
                    execute = argument.execute
                    map[argument] = arg
                    return@forEachIndexed
                }

            return CommandResult.Failure(
                translatable("commands.arg_doesnt_match", arg, i + 1)
            )
        }

        if (execute == null) return CommandResult.Failure(
            translatable("commands.incorrect_args_amount", id)
        )

        return execute(CommandContextImpl(map))
    }
}

fun Command(id: String, block: ArgumentBuilder.() -> Unit): Command = CommandImpl(id, block)



private sealed class ArgumentImpl(
    override val id: String
) : Argument {
    protected val argumentBuilder = ArgumentBuilderImpl()
}

private class LiteralArgumentImpl(
    id: String,
    block: ArgumentBuilder.() -> Unit
) : ArgumentImpl(id), LiteralArgument {
    override val children: List<Argument>
    override val execute: (CommandContext.() -> CommandResult)?

    init {
        argumentBuilder.block()
        children = argumentBuilder.arguments
        execute = argumentBuilder.execute
    }
}

private class TypedArgumentImpl<T>(
    id: String,
    override val type: ArgumentType<T>,
    block: ArgumentBuilder.(TypedArgument<T>) -> Unit
) : ArgumentImpl(id), TypedArgument<T> {
    override val children: List<Argument>
    override val execute: (CommandContext.() -> CommandResult)?

    init {
        argumentBuilder.block(this)
        children = argumentBuilder.arguments
        execute = argumentBuilder.execute
    }
}



private class CommandContextImpl(arguments: Map<TypedArgument<*>, String>) : CommandContext {
    private val arguments = arguments.toMap()

    override fun <T> getArgument(argument: TypedArgument<T>): T {
        return argument.type.parse(arguments[argument]!!)
    }
}



private class ArgumentBuilderImpl : ArgumentBuilder {
    var execute: (CommandContext.() -> CommandResult)? = null
    private var _arguments = mutableListOf<Argument>()
    val arguments get() = _arguments.toList()

    override fun literal(id: String, block: ArgumentBuilder.() -> Unit) {
        _arguments += LiteralArgumentImpl(id, block)
    }

    override fun <T> argument(
        id: String,
        type: ArgumentType<T>,
        block: ArgumentBuilder.(TypedArgument<T>) -> Unit
    ) {
        check(_arguments.filterIsInstance<TypedArgument<*>>().isEmpty()) {
            "Command can't have multiple typed arguments in the same branch"
        }
        _arguments += TypedArgumentImpl(id, type, block)
    }

    override fun executes(execute: CommandContext.() -> CommandResult) {
        this.execute = execute
    }
}