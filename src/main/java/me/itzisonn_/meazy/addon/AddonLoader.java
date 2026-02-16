package me.itzisonn_.meazy.addon;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.util.logger.LogLevel;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Represents an AddonLoader, allowing addons in the form of .jar
 */
@NullMarked
public class AddonLoader {
    private final Map<String, Class<?>> classesCache = new HashMap<>();
    private final Map<String, AddonClassLoader> loaders = new LinkedHashMap<>();

    public Addon loadAddon(File file) throws InvalidAddonException {
        if (!file.exists()) throw new InvalidAddonException(new FileNotFoundException(file.getPath() + " doesn't exist"));

        AddonInfo addonInfo;
        try {
            addonInfo = getAddonInfo(file);
        }
        catch (InvalidAddonInfoException e) {
            throw new InvalidAddonException(e);
        }

        File dataFolder = new File(file.getParentFile(), addonInfo.getId());
        if (dataFolder.exists() && !dataFolder.isDirectory()) {
            throw new InvalidAddonException("Datafolder '" + dataFolder + "' for " + addonInfo.getFullName() + " exists and isn't a directory");
        }

        for (String addonName : addonInfo.getDepend()) {
            if (loaders.get(addonName) == null) throw new UnknownDependencyException(addonName);
        }

        AddonClassLoader loader;
        try {
            loader = new AddonClassLoader(this, addonInfo, dataFolder, file);
        }
        catch (Throwable e) {
            throw new InvalidAddonException(e);
        }

        loaders.put(addonInfo.getId(), loader);
        return loader.getAddon();
    }

    /**
     *
     * @param file Addon's jar file
     * @return Addon info contained in the given file
     * @throws InvalidAddonInfoException If given file is invalid
     */
    public AddonInfo getAddonInfo(File file) throws InvalidAddonInfoException {
        try (JarFile jar = new JarFile(file)) {
            JarEntry entry = jar.getJarEntry("addon.json");
            if (entry == null) throw new InvalidAddonInfoException("Addon jar doesn't contain addon.json");

            try (InputStream inputStream = jar.getInputStream(entry)) {
                return new AddonInfo(inputStream);
            }
        }
        catch (IOException e) {
            throw new InvalidAddonInfoException(e);
        }
    }

    @Nullable
    public Class<?> getClassByName(String name, @Nullable AddonClassLoader exclude) {
        Class<?> cachedClass = classesCache.get(name);
        if (cachedClass != null) return cachedClass;

        for (AddonClassLoader loader : loaders.values()) {
            if (loader == exclude) continue;

            try {
                cachedClass = loader.findClass(name, false);
            }
            catch (ClassNotFoundException _) {
                continue;
            }

            if (cachedClass != null) {
                classesCache.put(name, cachedClass);
                return cachedClass;
            }
        }

        return null;
    }

    /**
     * Enables given addon
     * @param addon Addon to enable
     * @throws IllegalStateException If given addon has already been enabled
     */
    public void enableAddon(Addon addon) throws IllegalStateException {
        if (addon.isEnabled()) throw new IllegalStateException("Addon has already been enabled");

        try {
            MeazyMain.LOGGER.log(LogLevel.INFO, Text.translatable("meazy:addons.enabling", addon.getAddonInfo().getFullName()));
            addon.enable();
        }
        catch (Throwable e) {
            MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:addons.failed_enable", addon.getAddonInfo().getFullName(), e));
        }
    }
}