package me.itzisonn_.meazy.logging;

import me.itzisonn_.meazy.lang.text.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger for {@link Text} messages
 */
public class Logger {
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final String id;

    /**
     * @param id Id
     * @throws NullPointerException If given id is null
     */
    public Logger(String id) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");
        this.id = id;
    }


    /**
     * Logs text with given level to console
     *
     * @param level Level of logging
     * @param text Text to log
     *
     * @throws NullPointerException If either level or text is null
     */
    public void log(LogLevel level, Text text) throws NullPointerException {
        if (level == null) throw new NullPointerException("Level can't be null");
        if (text == null) throw new NullPointerException("Text can't be null");
        rawLog(level, text.toString());
    }

    private void rawLog(LogLevel logLevel, String text) {
        LocalDateTime now = LocalDateTime.now();
        String time = now.format(DATE_TIME_FORMATTER);

        String level = logLevel.getId().toUpperCase();

        System.out.println(time + " [" + level + "] " + id + ": " + text);
    }
}
