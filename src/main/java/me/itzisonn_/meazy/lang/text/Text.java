package me.itzisonn_.meazy.lang.text;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lang.file_provider.LanguageFileProvider;
import me.itzisonn_.meazy.lang.bundle.Bundle;
import org.jspecify.annotations.NullMarked;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents text
 */
@NullMarked
public interface Text {
    /**
     * @return String representation of this text
     */
    String toString();

    /**
     * Appends to this text given text
     * 
     * @param text Text to append
     * @return New text
     */
    default Text append(Text text) {
        return new MergedText(List.of(this, text));
    }



    /**
     * Returns text that is the same across multiple languages
     *
     * @param text Text
     * @return Literal text
     */
    static Text literal(String text, Object... args) {
        return new LiteralText(text, convertArgs(args));
    }

    /**
     * Returns text that can be translated in multiple languages
     *
     * @param key Translation key
     * @return Translatable text
     *
     * @throws IllegalArgumentException When can't find LanguageFileProvider with given id or
     *                                  when can't find bundle with LanguageFileProvider with given id
     */
    static Text translatable(String key, Object... args) throws IllegalArgumentException {
        String[] parts = key.split(":");
        if (parts.length != 2) throw new IllegalArgumentException("Invalid translation key " + key);

        String providerId = parts[0];
        String translationKey = parts[1];

        LanguageFileProvider languageFileProvider = MeazyMain.BUNDLE_MANAGER.getLanguageFileProvider(providerId);
        if (languageFileProvider == null) throw new IllegalArgumentException("Can't find LanguageFileProvider with given id");

        return translatable(languageFileProvider, translationKey, args);
    }

    /**
     * Returns text that can be translated in multiple languages
     *
     * @param languageFileProvider Provider for language file
     * @param key Translation key
     * @return Translatable text
     *
     * @throws IllegalArgumentException When can't find bundle with given languageFileProvider
     */
    static Text translatable(LanguageFileProvider languageFileProvider, String key, Object... args) throws IllegalArgumentException {
        Bundle bundle = MeazyMain.BUNDLE_MANAGER.getBundle(languageFileProvider);
        if (bundle == null) throw new IllegalArgumentException("Can't find bundle with given languageFileProvider");

        return new TranslatableText(bundle, key, convertArgs(args));
    }



    private static List<String> convertArgs(Object... args) {
        List<String> list = new ArrayList<>();

        for (Object arg : args) {
            if (arg instanceof Throwable throwable) {
                StringWriter writer = new StringWriter();
                throwable.printStackTrace(new PrintWriter(writer, true));
                list.add(writer.getBuffer().toString());
            }
            else list.add(String.valueOf(arg));
        }

        return list;
    }
}
