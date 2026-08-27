package me.itzisonn_.meazy.command

interface ArgumentType<T> {
    fun matches(string: String): Boolean
    fun parse(string: String): T
}

object StringType : ArgumentType<String> {
    override fun matches(string: String) = true
    override fun parse(string: String) = string
}