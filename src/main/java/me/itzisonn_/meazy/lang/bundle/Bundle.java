package me.itzisonn_.meazy.lang.bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.itzisonn_.meazy.FileUtils;
import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lang.file_provider.LanguageFileProvider;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.logging.LogLevel;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents translations bundle
 */
public class Bundle {
    private final BundleManager bundleManager;
    private final LanguageFileProvider languageFileProvider;
    private final Map<String, String> translations = new HashMap<>();

    private final static Gson gson = new Gson();
    @SuppressWarnings("unchecked")
    private final static TypeToken<Map<String, String>> mapTypeToken = (TypeToken<Map<String, String>>) TypeToken.getParameterized(Map.class, String.class, String.class);

    /**
     * @param bundleManager BundleManager
     * @param languageFileProvider LanguageFileProvider
     *
     * @throws NullPointerException If either bundleManager or languageFileProvider is null
     */
    public Bundle(BundleManager bundleManager, LanguageFileProvider languageFileProvider) throws NullPointerException {
        if (bundleManager == null) throw new NullPointerException("BundleManager can't be null");
        if (languageFileProvider == null) throw new NullPointerException("LanguageFileProvider can't be null");

        this.bundleManager = bundleManager;
        this.languageFileProvider = languageFileProvider;
        updateTranslations();
    }

    /**
     * Updates translations
     */
    public void updateTranslations() {
        translations.clear();

        try (InputStream inputStream = languageFileProvider.getLanguageFile(bundleManager.getLanguage())) {
            if (inputStream != null) translations.putAll(gson.fromJson(FileUtils.getLines(inputStream), mapTypeToken));
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to update translations", e);
        }

        if (translations.keySet().removeIf(key -> !key.matches("[a-zA-Z_.]+"))) {
            MeazyMain.LOGGER.log(LogLevel.INFO, Text.literal("Some of the keys were removed from bundle with provider {0} due to invalid format", languageFileProvider.getId()));
        }
    }

    /**
     * @param key Translation key
     * @return Translation that corresponds to given key
     */
    public String getTranslation(String key) {
        return translations.get(key);
    }

    /**
     * @param key Translation key
     * @param fallback Translation fallback
     * @return Translation that corresponds to given key or given fallback if it's null
     */
    public String getTranslationOrDefault(String key, String fallback) {
        return translations.getOrDefault(key, fallback);
    }
}
