package me.itzisonn_.meazy.settings;

import lombok.Getter;

@Getter
public class Settings {
    private final String language;
    private final boolean exceptionAbsentKey;

    public Settings(String language, boolean exceptionAbsentKey) {
        if (language != null) this.language = language;
        else this.language = "en";

        this.exceptionAbsentKey = exceptionAbsentKey;
    }
}
