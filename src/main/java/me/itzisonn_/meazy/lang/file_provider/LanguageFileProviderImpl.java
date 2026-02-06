package me.itzisonn_.meazy.lang.file_provider;

import lombok.Getter;
import me.itzisonn_.meazy.lang.Language;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.InputStream;
import java.util.function.Function;

/**
 * Implementation of {@link LanguageFileProvider}
 */
@NullMarked
public class LanguageFileProviderImpl implements LanguageFileProvider {
    @Getter
    private final String id;
    private final Function<String, @Nullable InputStream> resourceFunction;

    /**
     * @param id Id
     * @param resourceFunction Function that returns an input stream for reading the resource with specified path
     */
    public LanguageFileProviderImpl(String id, Function<String, @Nullable InputStream> resourceFunction) {
        this.id = id;
        this.resourceFunction = resourceFunction;
    }

    @Override
    @Nullable
    public InputStream getLanguageFile(Language language) {
        return resourceFunction.apply("lang/" + language.getId() + ".json");
    }
}
