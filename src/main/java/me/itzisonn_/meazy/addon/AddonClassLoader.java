package me.itzisonn_.meazy.addon;

import lombok.AccessLevel;
import lombok.Getter;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * A ClassLoader for addons to allow shared classes across multiple addons
 */
public final class AddonClassLoader extends URLClassLoader {
    private final AddonLoader loader;
    @Getter(AccessLevel.PACKAGE)
    private final AddonInfo addonInfo;
    @Getter(AccessLevel.PACKAGE)
    private final File dataFolder;
    @Getter(AccessLevel.PACKAGE)
    private final File file;
    @Getter
    private final Addon addon;
    private final Map<String, Class<?>> classesCache = new HashMap<>();

    public AddonClassLoader(AddonLoader loader, AddonInfo addonInfo, File dataFolder, File file) throws InvalidAddonException {
        if (loader == null) throw new NullPointerException("Loader can't be null");
        if (addonInfo == null) throw new NullPointerException("AddonInfo can't be null");
        if (dataFolder == null) throw new NullPointerException("DataFolder can't be null");
        if (file == null) throw new NullPointerException("File can't be null");

        URL url;
        try {
            url = file.toURI().toURL();
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        super("AddonClassLoader(" + addonInfo.getId() + ")", new URL[]{url}, loader.getClass().getClassLoader());

        this.loader = loader;
        this.addonInfo = addonInfo;
        this.dataFolder = dataFolder;
        this.file = file;

        Class<? extends Addon> addonClass;
        try {
            addonClass = Class.forName(addonInfo.getClassName(), true, this).asSubclass(Addon.class);
        }
        catch (ClassNotFoundException e) {
            throw new InvalidAddonException("Can't find main class '" + addonInfo.getClassName() + "' of addon " + addonInfo.getId(), e);
        }
        catch (ClassCastException e) {
            throw new InvalidAddonException("Main class of addon " + addonInfo.getId() + " doesn't extend Addon class", e);
        }

        try {
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
        if (name.startsWith("me.itzisonn_.meazy.")) throw new ClassNotFoundException(name);

        Class<?> result = classesCache.get(name);
        if (result != null) return result;

        if (checkGlobal) result = loader.getClassByName(name, this);
        if (result == null) result = super.findClass(name);

        classesCache.put(name, result);
        return result;
    }

    @Override
    public URL getResource(String name) {
        if (name == null) throw new NullPointerException("Name can't be null");

        URL url = findResource(name);
        if (url != null) return url;

        return super.getResource(name);
    }
}