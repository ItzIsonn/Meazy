package me.itzisonn_.meazy.util.logger;

import lombok.Getter;
import org.jspecify.annotations.NullMarked;

/**
 * Level of logging. Shows how important a message is
 */
@NullMarked
public enum LogLevel {
    INFO("info"),
    WARNING("warning"),
    ERROR("error");

    @Getter
    private final String id;

    LogLevel(String id) {
        this.id = id;
    }
}
