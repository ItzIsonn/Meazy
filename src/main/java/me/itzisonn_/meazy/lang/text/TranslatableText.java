package me.itzisonn_.meazy.lang.text;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lang.bundle.Bundle;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class TranslatableText implements Text {
    private final Bundle bundle;
    private final String key;
    private final List<String> args;

    TranslatableText(Bundle bundle, String key, List<String> args) throws IllegalArgumentException {
        if (key.isBlank()) throw new IllegalArgumentException("Key can't be blank");

        this.bundle = bundle;
        this.key = key;
        this.args = List.copyOf(args);
    }

    @Override
    public String toString() {
        String translation;

        if (MeazyMain.SETTINGS_MANAGER.getSettings().getExceptionAbsentKey()) {
            translation = bundle.getTranslation(key);
            if (translation == null) throw new TranslationNotFoundException("Can't find translation with key " + key);
        }
        else translation = bundle.getTranslationOrDefault(key, "?" + key + "?");

        for (int i = 0; i < args.size(); i++) {
            translation = translation.replace("{" + i + "}", args.get(i));
        }

        return translation;
    }
}
