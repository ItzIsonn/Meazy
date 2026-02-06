package me.itzisonn_.meazy.logging;

import me.itzisonn_.meazy.lang.text.Text;
import org.jspecify.annotations.NullMarked;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger for {@link Text} messages
 */
@NullMarked
public class Logger {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final String id;

    /**
     * @param id Id
     */
    public Logger(String id) {
        this.id = id;
    }


    /**
     * Logs text with given level to console
     *
     * @param level Level of logging
     * @param text Text to log
     */
    public void log(LogLevel level, Text text) {
        String time = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String levelString = level.getId().toUpperCase();
        System.out.println(time + " [" + levelString + "] " + id + ": " + text);
    }
}
