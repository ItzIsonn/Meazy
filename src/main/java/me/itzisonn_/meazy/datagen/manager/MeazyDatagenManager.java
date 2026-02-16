package me.itzisonn_.meazy.datagen.manager;

import me.itzisonn_.meazy.util.FileUtils;
import org.jspecify.annotations.NullMarked;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides methods for working with datagen
 */
@NullMarked
public class MeazyDatagenManager extends DatagenManager {
    @Override
    public Set<String> getDatagenFilesLines(String folderPath) {
        URL url = MeazyDatagenManager.class.getResource("/data/" + folderPath);
        if (url == null) throw new RuntimeException("Can't find file: " + folderPath);

        try (Stream<Path> paths = Files.walk(Path.of(url.toURI()))) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(FileUtils::getLines)
                    .collect(Collectors.toSet());
        }
        catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
