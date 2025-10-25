package me.itzisonn_.meazy.lang.file_provider;

import lombok.Getter;
import me.itzisonn_.meazy.lang.Language;

import java.io.InputStream;
import java.util.function.Function;

/**
 * Implementation of {@link LanguageFileProvider}
 */
public class LanguageFileProviderImpl implements LanguageFileProvider {
    @Getter
    private final String id;
    private final Function<String, InputStream> resourceFunction;

    /**
     * @param id Id
     * @param resourceFunction Function that returns an input stream for reading the resource with specified path
     *
     * @throws NullPointerException If either id or resourceFunction is null
     */
    public LanguageFileProviderImpl(String id, Function<String, InputStream> resourceFunction) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");
        if (resourceFunction == null) throw new NullPointerException("ResourceFunction can't be null");

        this.id = id;
        this.resourceFunction = resourceFunction;
    }

    @Override
    public InputStream getLanguageFile(Language language) {
        return resourceFunction.apply("lang/" + language.getId() + ".json");
    }
}
