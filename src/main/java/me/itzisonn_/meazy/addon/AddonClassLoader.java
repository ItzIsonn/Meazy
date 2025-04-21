package me.itzisonn_.meazy.addon;

import me.itzisonn_.meazy.addon.addon_info.AddonInfo;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A ClassLoader for addons to allow shared classes across multiple addons
 */
public final class AddonClassLoader extends URLClassLoader {
    private final AddonLoader loader;
    private final Map<String, Class<?>> classes = new HashMap<>();
    private final AddonInfo addonInfo;
    private final List<String> datagenFilesLines;
    private final File dataFolder;
    private final File file;
    final Addon addon;
    private Addon addonInit;

    public AddonClassLoader(final AddonLoader loader, final ClassLoader parent, final AddonInfo addonInfo, List<String> datagenFilesLines, final File dataFolder, final File file) throws InvalidAddonException, MalformedURLException {
        super(new URL[] {file.toURI().toURL()}, parent);
        if (loader == null) throw new IllegalArgumentException("Loader can't be null");

        this.loader = loader;
        this.addonInfo = addonInfo;
        this.datagenFilesLines = datagenFilesLines;
        this.dataFolder = dataFolder;
        this.file = file;

        try {
            Class<?> jarClass;
            try {
                jarClass = Class.forName(addonInfo.getMain(), true, this);
            }
            catch (ClassNotFoundException e) {
                throw new InvalidAddonException("Can't find main class '" + addonInfo.getMain() + "'", e);
            }

            Class<? extends Addon> addonClass;
            try {
                addonClass = jarClass.asSubclass(Addon.class);
            }
            catch (ClassCastException e) {
                throw new InvalidAddonException("Main class '" + addonInfo.getMain() + "' doesn't extend Addon", e);
            }

            addon = addonClass.getDeclaredConstructor().newInstance();
        }
        catch (IllegalAccessException e) {
            throw new InvalidAddonException("No public constructor", e);
        }
        catch (InstantiationException e) {
            throw new InvalidAddonException("Abnormal addon type", e);
        }
        catch (InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    public Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
        if (name.startsWith("me.itzisonn_.meazy.")) {
            throw new ClassNotFoundException(name);
        }
        Class<?> result = classes.get(name);

        if (result == null) {
            if (checkGlobal) {
                result = loader.getClassByName(name);
            }

            if (result == null) {
                result = super.findClass(name);

                if (result != null) {
                    loader.setClass(name, result);
                }
            }

            classes.put(name, result);
        }

        return result;
    }

    /**
     * @return All stored classes
     */
    public Set<String> getClasses() {
        return classes.keySet();
    }

    /**
     * Initializes given addon
     *
     * @param addon Addon to initialize
     */
    public synchronized void initialize(Addon addon) {
        if (addon == null) throw new IllegalArgumentException("Initializing addon can't be null");
        if (addon.getClass().getClassLoader() != this) throw new IllegalArgumentException("Can't initialize addon outside of this class loader");

        if (this.addon != null || this.addonInit != null) {
            throw new IllegalArgumentException("Addon has already been initialized");
        }

        this.addonInit = addon;

        addon.init(loader, addonInfo, datagenFilesLines, dataFolder, file, this);
    }
}