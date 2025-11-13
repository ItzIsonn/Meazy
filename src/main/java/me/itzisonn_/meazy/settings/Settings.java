package me.itzisonn_.meazy.settings;

import lombok.Getter;

@Getter
public class Settings {
    private final String language;
    private final boolean exceptionAbsentKey;
    private final boolean enableDefaultAddon;

    public Settings(String language, boolean exceptionAbsentKey, boolean enableDefaultAddon) {
        if (language != null) this.language = language;
        else this.language = "en";

        this.exceptionAbsentKey = exceptionAbsentKey;
        this.enableDefaultAddon = enableDefaultAddon;
    }
}
