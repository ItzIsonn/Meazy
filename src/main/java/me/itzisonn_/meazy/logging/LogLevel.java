package me.itzisonn_.meazy.logging;

import lombok.Getter;

/**
 * Level of logging. Shows how important a message is
 */
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
