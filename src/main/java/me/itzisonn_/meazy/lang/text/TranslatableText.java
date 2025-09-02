package me.itzisonn_.meazy.lang.text;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lang.bundle.Bundle;

/**
 * Represents text that can be translated in multiple languages
 */
public class TranslatableText implements Text {
    private final Bundle bundle;
    private final String key;

    /**
     *
     * @param bundle Bundle that contains given key
     * @param key Translation key
     *
     * @throws NullPointerException If either bundle or key is null
     * @throws IllegalArgumentException If given key is blank
     */
    protected TranslatableText(Bundle bundle, String key) throws NullPointerException, IllegalArgumentException {
        if (bundle == null) throw new NullPointerException("Bundle can't be null");
        if (key == null) throw new NullPointerException("Key can't be null");
        if (key.isBlank()) throw new IllegalArgumentException("Key can't be blank");

        this.bundle = bundle;
        this.key = key;
    }

    @Override
    public String getContent() {
        if (MeazyMain.SETTINGS_MANAGER.getSettings().isExceptionAbsentKey()) {
            String translation = bundle.getTranslation(key);
            if (translation == null) throw new TranslationNotFoundException("Can't find translation with key " + key);
            else return translation;
        }
        else return bundle.getTranslationOrDefault(key, "?" + key + "?");
    }

    @Override
    public String toString() {
        return "TranslatableText(key=" + key + ")";
    }
}
