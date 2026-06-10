package me.itzisonn_.meazy.runtime

import me.itzisonn_.meazy.text.Text
import me.itzisonn_.meazy.text.TextException

/**
 * Is thrown when resolver can't find object with requested identifier
 * @param text Text
 */
class InvalidIdentifierException(text: Text) : TextException(text)
