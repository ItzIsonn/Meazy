package me.itzisonn_.meazy.lang.file_provider;

import me.itzisonn_.meazy.lang.Language;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.InputStream;

/**
 * Represents provider for language files
 */
@NullMarked
public interface LanguageFileProvider {
    /**
     * @return Id of provider
     */
    String getId();

    /**
     * @param language Language
     * @return InputStream of file that contains translations for given language
     */
    @Nullable
    InputStream getLanguageFile(Language language);
}
