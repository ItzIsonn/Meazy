package me.itzisonn_.meazy.datagen.manager;

import me.itzisonn_.meazy.util.FileUtils;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * Provides methods for working with datagen
 */
@NullMarked
public class AddonDatagenManager extends DatagenManager {
    /**
     * Addon's file
     */
    private final File file;

    /**
     * @param file Addon's file
     */
    public AddonDatagenManager(File file) {
        this.file = file;
    }

    @Override
    public Set<String> getDatagenFilesLines(String folderPath) {
        Set<String> result = new HashSet<>();

        try (ZipFile zipFile = new ZipFile(file)) {
            ZipInputStream inputStream = new ZipInputStream(new FileInputStream(file));

            ZipEntry zipEntry = inputStream.getNextEntry();
            while (zipEntry != null) {
                if (!zipEntry.getName().startsWith("data/" + folderPath + "/") || zipEntry.isDirectory()) {
                    zipEntry = inputStream.getNextEntry();
                    continue;
                }

                result.add(FileUtils.getLines(zipFile.getInputStream(zipEntry)));
                zipEntry = inputStream.getNextEntry();
            }

            return result;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
