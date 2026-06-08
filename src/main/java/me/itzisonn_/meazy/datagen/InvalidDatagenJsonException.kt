package me.itzisonn_.meazy.datagen

/**
 * Is thrown when one of [DatagenDeserializers] meets invalid JSON object
 * @param message Message
 */
class InvalidDatagenJsonException(message: String) : RuntimeException(message)
