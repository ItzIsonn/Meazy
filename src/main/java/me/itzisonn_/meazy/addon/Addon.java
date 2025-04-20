package me.itzisonn_.meazy.addon;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.Getter;
import me.itzisonn_.meazy.addon.addon_info.AddonInfo;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an Addon
 */
public abstract class Addon {
    private boolean isEnabled = false;
    private AddonLoader loader = null;
    private File file = null;
    @Getter
    private List<InputStream> datagenInputStreams = new ArrayList<>();
    private AddonInfo addonInfo = null;
    private File dataFolder = null;
    private ClassLoader classLoader = null;
    private JsonElement config = null;
    private File configFile = null;
    private Logger logger = null;

    public Addon() {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        if (!(classLoader instanceof AddonClassLoader addonClassLoader)) throw new IllegalStateException("Addon requires " + AddonClassLoader.class.getName());
        addonClassLoader.initialize(this);
    }

    /**
     * Gets the associated AddonLoader responsible for this addon
     *
     * @return AddonLoader that controls this addon
     */
    public final AddonLoader getAddonLoader() {
        return loader;
    }

    /**
     * Returns a value indicating whether or not this addon is currently
     * enabled
     *
     * @return true if this addon is enabled, otherwise false
     */
    public final boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Returns the file which contains this addon
     *
     * @return File containing this addon
     */
    protected File getFile() {
        return file;
    }

    /**
     * Returns the {@link AddonInfo} class containing the info for this addon
     *
     * @return Contents of the addon.json file
     */
    public final AddonInfo getAddonInfo() {
        return addonInfo;
    }

    /**
     * Returns the ClassLoader which holds this addon
     *
     * @return ClassLoader holding this addon
     */
    protected final ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Returns the folder that the addon data's files are located in. The
     * folder may not yet exist.
     *
     * @return The folder.
     */
    public final File getDataFolder() {
        return dataFolder;
    }

    public JsonElement getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return config;
    }

    public void reloadConfig() {
        StringBuilder json = new StringBuilder();
        try (FileInputStream inputStream = new FileInputStream(configFile)) {
            for (String line : new BufferedReader(new InputStreamReader(inputStream)).lines().toList()) {
                json.append(line);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        config = JsonParser.parseString(json.toString());
    }

    public void saveConfig() {
        if (configFile == null) throw new IllegalArgumentException("File cannot be null");

        try {
            File parent = configFile.getCanonicalFile().getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() || !parent.isDirectory()) {
                    throw new IOException("Unable to create parent directories of " + configFile);
                }
            }

            String data = config.toString();

            try (Writer writer = new OutputStreamWriter(new FileOutputStream(configFile), Charset.defaultCharset())) {
                writer.write(data);
            }
        }
        catch (IOException ex) {
            logger.log(Level.ERROR, "Couldn't save config to {}", configFile, ex);
        }
    }

    public void saveDefaultConfig() {
        if (!configFile.exists()) {
            saveResource("config.json", false);
        }
    }

    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.isEmpty()) {
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
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            }
            else {
                logger.log(Level.WARN, "Couldn't save {} to {} because {} already exists", outFile.getName(), outFile, outFile.getName());
            }
        }
        catch (IOException ex) {
            logger.log(Level.ERROR, "Couldn't save {} to {}", outFile.getName(), outFile, ex);
        }
    }

    public InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename can't be null");
        }

        try {
            URL url = getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        }
        catch (IOException ex) {
            return null;
        }
    }

    public abstract void onInitialize();

    /**
     * Enables this addon
     */
    protected final void enable() throws AddonEnableException {
        if (!isEnabled) {
            isEnabled = true;
            onInitialize();
        }
        else throw new AddonEnableException("Addon has already been enabled");
    }

    public final void init(AddonLoader loader, AddonInfo addonInfo, List<InputStream> datagenInputStreams, File dataFolder, File file, ClassLoader classLoader) {
        this.loader = loader;
        this.file = file;
        this.addonInfo = addonInfo;
        this.datagenInputStreams = datagenInputStreams;
        this.dataFolder = dataFolder;
        this.classLoader = classLoader;
        this.configFile = new File(dataFolder, "config.json");
        this.logger = LogManager.getLogger(addonInfo.getId());
    }

    /**
     * @return This addon's logger
     */
    public final Logger getLogger() {
        return logger;
    }

    /**
     * Returns addon's full name
     *
     * @return {@link AddonInfo#getFullName()}
     */
    @Override
    public String toString() {
        return addonInfo.getFullName();
    }

    /**
     * This method provides fast access to the addon that has {@link #getProvidingAddon(Class) provided}
     * the given addon class, which is usually the addon that implemented it.
     *
     * @param addonClass The class desired
     * @return The addon that provides and implements given class
     * @throws IllegalArgumentException If addonClass is null or doesn't extend {@link Addon}
     * @throws IllegalStateException If addonClass wasn't provided by an addon,
     *     for example, if called with
     *     <code>Addon.getAddon(Addon.class)</code>
     *     or called from the static initializer for given Addon
     * @throws ClassCastException If addon that provided the class doesn't extend the class
     */
    public static <T extends Addon> T getAddon(Class<T> addonClass) {
        if (addonClass == null) throw new IllegalArgumentException("Null class can't have a addon");

        if (!Addon.class.isAssignableFrom(addonClass)) {
            throw new IllegalArgumentException(addonClass + " doesn't extend " + Addon.class);
        }
        final ClassLoader cl = addonClass.getClassLoader();
        if (!(cl instanceof AddonClassLoader)) {
            throw new IllegalArgumentException(addonClass + " isn't initialized by " + AddonClassLoader.class);
        }
        Addon addon = ((AddonClassLoader) cl).addon;
        if (addon == null) {
            throw new IllegalStateException("Can't get addon for " + addonClass + " from a static initializer");
        }
        return addonClass.cast(addon);
    }

    /**
     * This method provides fast access to the addon that has provided the given class.
     *
     * @throws IllegalArgumentException If the class is null or isn't provided by an Addon
     * @throws IllegalStateException If called from the static initializer for given Addon
     */
    public static Addon getProvidingAddon(Class<?> addonClass) {
        if (addonClass == null) throw new IllegalArgumentException("Null class can't have a addon");

        ClassLoader cl = addonClass.getClassLoader();
        if (!(cl instanceof AddonClassLoader)) {
            throw new IllegalArgumentException(addonClass + " isn't provided by " + AddonClassLoader.class);
        }
        Addon addon = ((AddonClassLoader) cl).addon;
        if (addon == null) {
            throw new IllegalStateException("Can't get addon for " + addonClass + " from a static initializer");
        }
        return addon;
    }
}