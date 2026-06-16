package me.itzisonn_.meazy.command

interface ArgumentType<T> {
    fun matches(string: String): Boolean
    fun parse(string: String): T
}

object StringType : ArgumentType<String> {
    override fun matches(string: String) = true
    override fun parse(string: String) = string
}

object IntType : ArgumentType<Int> {
    override fun matches(string: String) = string.toIntOrNull() != null
    override fun parse(string: String) = string.toIntOrNull() ?: error("Failed to parse $string to int type")
}