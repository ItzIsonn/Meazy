package me.itzisonn_.meazy.runtime

import me.itzisonn_.meazy.text.Text
import me.itzisonn_.meazy.text.TextException

/**
 * Is thrown TODO
 * @param text Text
 */
class EvaluationException(text: Text) : TextException(text)