package me.itzisonn_.meazy.lang.bundle;

import lombok.Getter;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.lang.Language;
import me.itzisonn_.meazy.lang.file_provider.LanguageFileProvider;
import me.itzisonn_.registry.RegistryEntry;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents bundle manager
 */
public class BundleManager {
    /**
     * Language
     */
    @Getter
    private Language language;
    private final Map<LanguageFileProvider, Bundle> bundles;

    /**
     * Main constructor
     *
     * @param language Language
     * @throws NullPointerException If given language is null
     */
    public BundleManager(Language language, LanguageFileProvider... languageFileProviders) throws NullPointerException {
        if (language == null) throw new NullPointerException("Language can't be null");

        this.language = language;
        bundles = new HashMap<>();

        if (languageFileProviders == null) return;
        for (LanguageFileProvider languageFileProvider : languageFileProviders) {
            addLanguageFileProvider(languageFileProvider);
        }
    }

    /**
     * Constructor with language set to English
     */
    public BundleManager(LanguageFileProvider... languageFileProviders) {
        this(getDefaultLanguage(), languageFileProviders);
    }

    private static Language getDefaultLanguage() {
        RegistryEntry<Language> entry = Registries.LANGUAGES.getEntry("en");
        if (entry != null) return entry.getValue();
        else return new Language("en", "English");
    }

    /**
     * Adds given languageFileProvider
     * @param languageFileProvider LanguageFileProvider
     *
     * @throws NullPointerException If given languageFileProvider is null
     * @throws IllegalArgumentException If LanguageFileProvider with given languageFileProvider's id already exist
     */
    public void addLanguageFileProvider(LanguageFileProvider languageFileProvider) throws NullPointerException, IllegalArgumentException {
        if (languageFileProvider == null) throw new NullPointerException("LanguageFileProvider can't be null");
        if (getLanguageFileProvider(languageFileProvider.getId()) != null) {
            throw new IllegalArgumentException("LanguageFileProvider with given id already exists");
        }

        bundles.put(languageFileProvider, new Bundle(this, languageFileProvider));
    }

    /**
     * @param id Id
     * @return LanguageFileProvider with given id or null
     * @throws NullPointerException If given id is null
     */
    public LanguageFileProvider getLanguageFileProvider(String id) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");

        for (LanguageFileProvider languageFileProvider : bundles.keySet()) {
            if (languageFileProvider.getId().equals(id)) return languageFileProvider;
        }

        return null;
    }

    /**
     * Updates language
     *
     * @param language Language
     * @throws NullPointerException If given language is null
     */
    public void setLanguage(Language language) throws NullPointerException {
        if (language == null) throw new NullPointerException("Language can't be null");
        this.language = language;

        for (Bundle bundle : bundles.values()) {
            bundle.updateTranslations();
        }
    }

    /**
     * @param languageFileProvider LanguageFileProvider
     * @return Bundle that corresponds to given languageFileProvider
     *
     * @throws NullPointerException If given languageFileProvider is null
     */
    public Bundle getBundle(LanguageFileProvider languageFileProvider) throws NullPointerException {
        if (languageFileProvider == null) throw new NullPointerException("LanguageFileProvider can't be null");
        return bundles.get(languageFileProvider);
    }
}
