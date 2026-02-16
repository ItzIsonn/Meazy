package me.itzisonn_.meazy.settings;

import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@Getter
@NullMarked
public class Settings {
    private final String language;
    private final boolean exceptionAbsentKey;

    public Settings(@Nullable String language, boolean exceptionAbsentKey) {
        if (language != null) this.language = language;
        else this.language = "en";

        this.exceptionAbsentKey = exceptionAbsentKey;
    }
}
