package me.itzisonn_.meazy.runtime

import me.itzisonn_.meazy.util.text.Text
import me.itzisonn_.meazy.util.text.TextException

/**
 * Is thrown TODO
 * @param text Text
 */
class EvaluationException(text: Text) : TextException(text)