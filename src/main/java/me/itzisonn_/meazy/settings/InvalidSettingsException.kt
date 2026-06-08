package me.itzisonn_.meazy.settings

/**
 * Is thrown when [SettingsDeserializer] meets invalid settings file
 * @param message Message
 */
class InvalidSettingsException(message: String) : RuntimeException(message)
