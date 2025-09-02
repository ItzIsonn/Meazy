package me.itzisonn_.meazy.lang.text;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lang.file_provider.LanguageFileProvider;
import me.itzisonn_.meazy.lang.bundle.Bundle;

import java.util.List;

/**
 * Represents text
 */
public interface Text {
    /**
     * @return Content of this text
     */
    String getContent();

    /**
     * Replaces placeholders in content with given args
     *
     * @param args Args
     * @return Parsed content of this text
     */
    default String getContent(Object... args) {
        String content = getContent();

        for (int i = 0; i < args.length; i++) {
            content = content.replace("{" + i + "}", String.valueOf(args[i]));
        }

        return content;
    }

    /**
     * Appends to this text given text
     * 
     * @param text Text to append
     * @return New text
     * 
     * @throws NullPointerException If given text is null
     */
    default Text append(Text text) throws NullPointerException {
        return new MergedText(List.of(this, text));
    }



    /**
     * Returns text that is the same across multiple languages
     *
     * @param text Text
     * @return Literal text
     *
     * @throws NullPointerException If given text is null
     */
    static Text literal(String text) throws NullPointerException {
        return new LiteralText(text);
    }

    /**
     * Returns text that can be translated in multiple languages
     *
     * @param key Translation key
     * @return Translatable text
     *
     * @throws NullPointerException If given key is null
     * @throws IllegalArgumentException If can't find LanguageFileProvider with given id or
     *                                  if can't find bundle with LanguageFileProvider with given id
     */
    static Text translatable(String key) throws NullPointerException, IllegalArgumentException {
        if (key == null) throw new NullPointerException("Key can't be null");

        String[] parts = key.split(":");
        if (parts.length != 2) throw new IllegalArgumentException("Invalid key");

        String providerId = parts[0];
        String translationKey = parts[1];

        LanguageFileProvider languageFileProvider = MeazyMain.BUNDLE_MANAGER.getLanguageFileProvider(providerId);
        if (languageFileProvider == null) throw new IllegalArgumentException("Can't find LanguageFileProvider with given id");

        return translatable(languageFileProvider, translationKey);
    }

    /**
     * Returns text that can be translated in multiple languages
     *
     * @param languageFileProvider Provider for language file
     * @param key Translation key
     * @return Translatable text
     *
     * @throws NullPointerException If either languageFileProvider or key is null
     * @throws IllegalArgumentException If can't find bundle with given languageFileProvider
     */
    static Text translatable(LanguageFileProvider languageFileProvider, String key) throws NullPointerException, IllegalArgumentException {
        if (languageFileProvider == null) throw new NullPointerException("LanguageFileProvider can't be null");

        Bundle bundle = MeazyMain.BUNDLE_MANAGER.getBundle(languageFileProvider);
        if (bundle == null) throw new IllegalArgumentException("Can't find bundle with given languageFileProvider");

        return new TranslatableText(bundle, key);
    }
}
