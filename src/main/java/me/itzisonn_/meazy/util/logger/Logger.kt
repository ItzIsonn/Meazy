package me.itzisonn_.meazy.util.logger

import me.itzisonn_.meazy.lang.text.Text
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Logger for [Text] messages
 * @param id Id
 */
class Logger(private val id: String) {
    /**
     * Logs text with given level to console
     * 
     * @param level Level of logging
     * @param text Text to log
     */
    fun log(level: LogLevel, text: Text) {
        val time = LocalDateTime.now().format(DATE_TIME_FORMATTER)
        val levelString = level.id.uppercase(Locale.getDefault())
        println("$time [$levelString] $id: $text")
    }

    companion object {
        private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss")
    }
}
