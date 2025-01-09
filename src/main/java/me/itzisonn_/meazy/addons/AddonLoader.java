package me.itzisonn_.meazy.addons;

import me.itzisonn_.meazy.MeazyMain;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * Represents an AddonLoader, allowing addons in the form of .jar
 */
public class AddonLoader {
    private final Pattern[] fileFilters = new Pattern[] { Pattern.compile("\\.jar$"), };
    private final Map<String, Class<?>> classes = new HashMap<>();
    private final Map<String, AddonClassLoader> loaders = new LinkedHashMap<>();

    public Addon loadAddon(final File file) throws InvalidAddonException {
        if (file == null) throw new IllegalArgumentException("File can't be null");

        if (!file.exists()) {
            throw new InvalidAddonException(new FileNotFoundException(file.getPath() + " doesn't exist"));
        }

        final AddonInfo description;
        try {
            description = getAddonInfo(file);
        }
        catch (InvalidAddonInfoException e) {
            throw new InvalidAddonException(e);
        }

        final File parentFile = file.getParentFile();
        final File dataFolder = new File(parentFile, description.getId());

        if (dataFolder.exists() && !dataFolder.isDirectory()) {
            throw new InvalidAddonException("Projected datafolder: '" + dataFolder + "' for " + description.getFullName() + " (" + file + ") exists and isn't a directory");
        }

        for (final String addonName : description.getDepend()) {
            AddonClassLoader current = loaders.get(addonName);

            if (current == null) {
                throw new UnknownDependencyException(addonName);
            }
        }

        final AddonClassLoader loader;
        try {
            loader = new AddonClassLoader(this, getClass().getClassLoader(), description, dataFolder, file);
        }
        catch (InvalidAddonException e) {
            throw e;
        }
        catch (Throwable e) {
            throw new InvalidAddonException(e);
        }

        loaders.put(description.getId(), loader);

        return loader.addon;
    }

    /**
     *
     * @param file Addon's jar file
     * @return Addon info contained in the given file
     * @throws InvalidAddonInfoException When incorrect jar file is presented
     */
    public AddonInfo getAddonInfo(File file) throws InvalidAddonInfoException {
        if (file == null) throw new IllegalArgumentException("File can't be null");

        InputStream stream = null;

        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = jar.getJarEntry("addon.json");

            if (entry == null) {
                throw new InvalidAddonInfoException(new FileNotFoundException("Jar doesn't contain addon.json"));
            }

            stream = jar.getInputStream(entry);

            return new AddonInfo(stream);

        }
        catch (IOException e) {
            throw new InvalidAddonInfoException(e);
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                }
                catch (IOException ignored) {}
            }
        }
    }

    public Pattern[] getAddonFileFilters() {
        return fileFilters.clone();
    }

    public Class<?> getClassByName(final String name) {
        Class<?> cachedClass = classes.get(name);

        if (cachedClass != null) return cachedClass;
        else {
            for (String current : loaders.keySet()) {
                AddonClassLoader loader = loaders.get(current);

                try {
                    cachedClass = loader.findClass(name, false);
                }
                catch (ClassNotFoundException ignored) {}
                if (cachedClass != null) return cachedClass;
            }
        }
        return null;
    }

    public void setClass(final String name, final Class<?> clazz) {
        if (!classes.containsKey(name)) {
            classes.put(name, clazz);
        }
    }

    private void removeClass(String name) {
        classes.remove(name);
    }

    /**
     * Enables given addon
     *
     * @param addon Addon to enable
     */
    public void enableAddon(final Addon addon) {
        if (addon == null) throw new IllegalArgumentException("Addon is not associated with this AddonLoader");

        if (!addon.isEnabled()) {
            MeazyMain.getLogger().info("Enabling {}", addon.getAddonInfo().getFullName());

            String addonName = addon.getAddonInfo().getId();

            if (!loaders.containsKey(addonName)) {
                loaders.put(addonName, (AddonClassLoader) addon.getClassLoader());
            }

            try {
                addon.enable();
            }
            catch (Throwable e) {
                MeazyMain.getLogger().log(Level.ERROR, "Error occurred while enabling {}", addon.getAddonInfo().getFullName(), e);
            }
        }
    }
}