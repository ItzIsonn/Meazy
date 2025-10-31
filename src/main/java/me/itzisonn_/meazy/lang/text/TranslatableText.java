package me.itzisonn_.meazy.lang.text;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lang.bundle.Bundle;

import java.util.List;

/**
 * Represents text that can be translated in multiple languages
 */
public class TranslatableText implements Text {
    private final Bundle bundle;
    private final String key;
    private final List<String> args;

    /**
     *
     * @param bundle Bundle that contains given key
     * @param key Translation key
     *
     * @throws NullPointerException If either bundle or key is null
     * @throws IllegalArgumentException If given key is blank
     */
    protected TranslatableText(Bundle bundle, String key, List<String> args) throws NullPointerException, IllegalArgumentException {
        if (bundle == null) throw new NullPointerException("Bundle can't be null");
        if (key == null) throw new NullPointerException("Key can't be null");
        if (key.isBlank()) throw new IllegalArgumentException("Key can't be blank");
        if (args == null) throw new NullPointerException("Args can't be null");

        this.bundle = bundle;
        this.key = key;
        this.args = List.copyOf(args);
    }

    @Override
    public String toString() {
        String translation;

        if (MeazyMain.SETTINGS_MANAGER.getSettings().isExceptionAbsentKey()) {
            translation = bundle.getTranslation(key);
            if (translation == null) throw new TranslationNotFoundException("Can't find translation with key " + key);
        }
        else translation = bundle.getTranslationOrDefault(key, "?" + key + "?");

        for (int i = 0; i < args.size(); i++) {
            translation = translation.replace("{" + i + "}", String.valueOf(args.get(i)));
        }

        return translation;
    }
}
