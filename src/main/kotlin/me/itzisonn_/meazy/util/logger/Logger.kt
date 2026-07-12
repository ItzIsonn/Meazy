package me.itzisonn_.meazy.util.logger

import me.itzisonn_.meazy.util.text.Text
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Logger for [Text] messages
 */
object Logger {
    private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss")

    /**
     * Logs text with given level to console
     * 
     * @param level Level of logging
     * @param text Text to log
     */
    fun log(level: LogLevel, text: Text) {
        val time = LocalDateTime.now().format(DATE_TIME_FORMATTER)
        val levelString = level.id.uppercase()
        println("$time [$levelString]: $text")
    }
}
