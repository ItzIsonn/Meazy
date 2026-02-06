package me.itzisonn_.meazy.addon;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import me.itzisonn_.meazy.FileUtils;
import me.itzisonn_.meazy.logging.LogLevel;
import me.itzisonn_.meazy.logging.Logger;
import me.itzisonn_.meazy.addon.datagen.DatagenManager;
import me.itzisonn_.meazy.lang.file_provider.LanguageFileProvider;
import me.itzisonn_.meazy.lang.file_provider.LanguageFileProviderImpl;
import me.itzisonn_.meazy.lang.text.Text;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Represents an Addon
 */
@NullMarked
public abstract class Addon {
    private final AddonClassLoader classLoader;
    private final AddonInfo addonInfo;
    private final File dataFolder;
    private final File file;
    private final DatagenManager datagenManager;
    private final File configFile;
    private final Logger logger;
    private boolean isEnabled = false;
    @Nullable
    private JsonElement config;

    public Addon() {
        if (!(getClass().getClassLoader() instanceof AddonClassLoader addonClassLoader)) {
            throw new IllegalStateException("Addon must be loaded with " + AddonClassLoader.class.getName());
        }

        classLoader = addonClassLoader;
        addonInfo = addonClassLoader.getAddonInfo();
        dataFolder = addonClassLoader.getDataFolder();
        file = addonClassLoader.getFile();
        datagenManager = new DatagenManager(file);
        configFile = new File(dataFolder, "config.json");
        logger = new Logger(addonInfo.getId());
    }



    /**
     * @return AddonInfo of this addon
     */
    public final AddonInfo getAddonInfo() {
        return addonInfo;
    }

    /**
     * Returns the folder that the addon data's files are located in.
     * The folder may not yet exist.
     *
     * @return The folder
     */
    public final File getDataFolder() {
        return dataFolder;
    }

    /**
     * @return File that contains this addon
     */
    public final File getFile() {
        return file;
    }

    /**
     * @return Datagen manager
     */
    public final DatagenManager getDatagenManager() {
        return datagenManager;
    }



    /**
     * @return Addon's config
     */
    public JsonElement getConfig() {
        if (config == null) reloadConfig();
        return config;
    }

    public void reloadConfig() {
        config = JsonParser.parseString(FileUtils.getLines(configFile));
    }

    public void saveConfig() {
        try {
            File parent = configFile.getCanonicalFile().getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() || !parent.isDirectory()) {
                    throw new IOException("Unable to create parent directories of " + configFile);
                }
            }

            if (config == null) throw new NullPointerException("No config file found");
            String data = config.toString();

            try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile), Charset.defaultCharset())) {
                writer.write(data);
            }
        }
        catch (IOException e) {
            logger.log(LogLevel.ERROR, Text.translatable("meazy:addons.resource.save_failed_config", configFile.getAbsolutePath(), e));
        }
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            saveResource("config.json", false);
        }
    }

    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath.isEmpty()) {
            throw new IllegalArgumentException("ResourcePath can't be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + file);
        }

        File outFile = new File(dataFolder, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(dataFolder, resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists() && !outDir.mkdirs()) {
            throw new RuntimeException(new IOException("Can't create resource's directories"));
        }

        try {
            if (!outFile.exists() || replace) Files.copy(in, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            else logger.log(LogLevel.WARNING, Text.translatable("meazy:addons.resource.save_failed_already_exists", outFile));
        }
        catch (IOException e) {
            logger.log(LogLevel.ERROR, Text.translatable("meazy:addons.resource.save_failed", outFile.getAbsolutePath(), e));
        }
    }

    @Nullable
    public InputStream getResource(String filename) {
        try {
            URL url = classLoader.getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        }
        catch (IOException e) {
            return null;
        }
    }



    protected abstract void onEnable();

    /**
     * Enables this addon
     * @throws AddonEnableException If addon has already been enabled
     */
    final void enable() throws AddonEnableException {
        if (isEnabled) throw new AddonEnableException("Addon has already been enabled");

        onEnable();
        isEnabled = true;
    }

    /**
     * @return Whether this addon is enabled
     */
    public final boolean isEnabled() {
        return isEnabled;
    }



    /**
     * @return This addon's logger
     */
    public final Logger getLogger() {
        return logger;
    }

    /**
     * @return LanguageFileProvider for this addon
     */
    @Nullable
    public LanguageFileProvider getLanguageFileProvider() {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream("lang/")) {
            if (resource == null) return null;
        }
        catch (IOException e) {
            return null;
        }

        return new LanguageFileProviderImpl(addonInfo.getId(), getClass().getClassLoader()::getResourceAsStream);
    }



    @Override
    public String toString() {
        return addonInfo.getFullName();
    }
}