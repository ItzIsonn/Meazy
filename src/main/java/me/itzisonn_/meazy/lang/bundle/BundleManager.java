package me.itzisonn_.meazy.lang.bundle;

import lombok.Getter;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.lang.Language;
import me.itzisonn_.meazy.lang.file_provider.LanguageFileProvider;
import me.itzisonn_.registry.RegistryEntry;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents bundle manager
 */
@NullMarked
public class BundleManager {
    /**
     * Language
     */
    @Getter
    private Language language;
    private final Map<LanguageFileProvider, Bundle> bundles;

    /**
     * Main constructor
     * @param language Language
     * TODO javadoc
     */
    public BundleManager(Language language, LanguageFileProvider... languageFileProviders) {
        this.language = language;
        bundles = new HashMap<>();

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
     * @throws IllegalArgumentException If LanguageFileProvider with given languageFileProvider's id already exist
     */
    public void addLanguageFileProvider(LanguageFileProvider languageFileProvider) throws IllegalArgumentException {
        if (getLanguageFileProvider(languageFileProvider.getId()) != null) {
            throw new IllegalArgumentException("LanguageFileProvider with given id already exists");
        }

        bundles.put(languageFileProvider, new Bundle(this, languageFileProvider));
    }

    /**
     * @param id Id
     * @return LanguageFileProvider with given id or null
     */
    @Nullable
    public LanguageFileProvider getLanguageFileProvider(String id) {
        for (LanguageFileProvider languageFileProvider : bundles.keySet()) {
            if (languageFileProvider.getId().equals(id)) return languageFileProvider;
        }

        return null;
    }

    /**
     * Updates language
     * @param language New language
     */
    public void setLanguage(Language language) {
        this.language = language;

        for (Bundle bundle : bundles.values()) {
            bundle.updateTranslations();
        }
    }

    /**
     * @param languageFileProvider LanguageFileProvider
     * @return Bundle that corresponds to given languageFileProvider
     */
    @Nullable
    public Bundle getBundle(LanguageFileProvider languageFileProvider) {
        return bundles.get(languageFileProvider);
    }
}
