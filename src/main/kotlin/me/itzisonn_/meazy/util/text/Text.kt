package me.itzisonn_.meazy.util.text

import java.io.PrintWriter
import java.io.StringWriter

/**
 * Represents text
 */
sealed interface Text {
    /**
     * @return String representation of this text
     */
    override fun toString(): String

    /**
     * Appends to this text given text
     * 
     * @param text Text to append
     * @return New text
     */
    fun append(text: Text): Text
}



private class LiteralText(private val text: String, args: List<String>) : Text {
    private val args = args.toList()

    override fun toString(): String {
        var result = text

        for (i in args.indices) {
            result = result.replace("{$i}", args[i])
        }

        return result
    }

    override fun append(text: Text): Text {
        return MergedText(listOf(this, text))
    }
}

/**
 * Returns text that is the same across multiple languages
 *
 * @param text Text
 * @return Literal text
 */
fun literal(text: String, vararg args: Any): Text {
    return LiteralText(text, convertArgs(*args))
}



private class TranslatableText(private val key: String, args: List<String>) : Text {
    private val args = args.toMutableList()

    init {
        require(key.isNotBlank()) { "Key can't be blank" }
    }

    override fun toString(): String {
        var translation = Translations[key] ?: error("Can't find translation with key $key")

        for (i in args.indices) {
            translation = translation.replace("{$i}", args[i])
        }

        return translation
    }

    override fun append(text: Text): Text {
        return MergedText(listOf(this, text))
    }
}

/**
 * Returns text that can be translated in multiple languages
 *
 * @param key Translation key
 * @return Translatable text
 *
 * @throws IllegalArgumentException When can't find LanguageFileProvider with given id or
 * when can't find bundle with LanguageFileProvider with given id
 */
fun translatable(key: String, vararg args: Any): Text {
    return TranslatableText(key, convertArgs(*args))
}



private class MergedText(texts: List<Text>) : Text {
    private val texts = texts.toMutableList()

    init {
        require(texts.isNotEmpty()) { "Texts can't be empty" }
    }

    override fun toString(): String {
        return texts.joinToString("") { it.toString() }
    }

    override fun append(text: Text): Text {
        if (text is MergedText) texts.addAll(text.texts)
        else texts.add(text)

        return this
    }
}



private fun convertArgs(vararg args: Any): MutableList<String> {
    val list = mutableListOf<String>()

    for (arg in args) {
        if (arg is Throwable) {
            val writer = StringWriter()
            arg.printStackTrace(PrintWriter(writer, true))
            list.add(writer.buffer.toString())
        }
        else list.add(arg.toString())
    }

    return list
}
