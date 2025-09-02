package me.itzisonn_.meazy.lang.file_provider;

import me.itzisonn_.meazy.lang.Language;

import java.io.InputStream;

/**
 * Represents provider for language files
 */
public interface LanguageFileProvider {
    /**
     * @return Id of provider
     */
    String getId();

    /**
     * @param language Language
     * @return InputStream of file that contains translations for given language
     */
    InputStream getLanguageFile(Language language);
}
