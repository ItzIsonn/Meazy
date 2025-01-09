package me.itzisonn_.meazy.addons;

import me.itzisonn_.meazy.MeazyMain;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an AddonManager
 */
public final class AddonManager {
    private final Map<Pattern, AddonLoader> fileAssociations = new HashMap<>();
    private final List<Addon> addons = new ArrayList<>();
    private final Map<String, Addon> lookupIds = new HashMap<>();

    public AddonManager() {
        AddonLoader instance = new AddonLoader();

        Pattern[] patterns = instance.getAddonFileFilters();

        synchronized (this) {
            for (Pattern pattern : patterns) {
                fileAssociations.put(pattern, instance);
            }
        }
    }

    /**
     * Loads addons in given directory
     *
     * @param directory Directory with addons
     * @return Array of loaded addons
     */
    public Addon[] loadAddons(File directory) {
        if (directory == null) throw new IllegalArgumentException("Directory can't be null");
        if (!directory.isDirectory()) throw new IllegalArgumentException("Directory must be a directory");

        List<Addon> result = new ArrayList<>();
        Set<Pattern> filters = fileAssociations.keySet();

        Map<String, File> addons = new HashMap<>();
        Set<String> loadedAddons = new HashSet<>();
        Map<String, Collection<String>> dependencies = new HashMap<>();
        Map<String, Collection<String>> softDependencies = new HashMap<>();

        File[] listFiles = directory.listFiles();
        if (listFiles == null) throw new NullPointerException("Directory's list of files is null");
        for (File file : listFiles) {
            AddonLoader loader = null;
            for (Pattern filter : filters) {
                Matcher match = filter.matcher(file.getName());
                if (match.find()) {
                    loader = fileAssociations.get(filter);
                }
            }

            if (loader == null) continue;

            AddonInfo addonInfo;
            try {
                addonInfo = loader.getAddonInfo(file);
                String name = addonInfo.getId();
                if (name.equalsIgnoreCase("meazy")) {
                    MeazyMain.getLogger().log(Level.ERROR, "Couldn't load '{}' in folder '{}': Restricted Name",
                            file.getPath(), directory.getPath());
                    continue;
                }
                else if (addonInfo.getId().indexOf(' ') != -1) {
                    MeazyMain.getLogger().log(Level.WARN, "Addon `{}' uses the space-character (0x20) in its name `{}' - this is discouraged",
                        addonInfo.getFullName(), addonInfo.getId());
                }
            }
            catch (InvalidAddonInfoException e) {
                MeazyMain.getLogger().log(Level.ERROR, "Couldn't load '{}' in folder '{}'",
                        file.getPath(), directory.getPath(), e);
                continue;
            }

            File replacedFile = addons.put(addonInfo.getId(), file);
            if (replacedFile != null) {
                MeazyMain.getLogger().log(Level.ERROR, "Ambiguous addon name `{}' for files `{}' and `{}' in `{}'",
                    addonInfo.getId(), file.getPath(), replacedFile.getPath(), directory.getPath());
            }

            Collection<String> softDependencySet = addonInfo.getSoftDepend();
            if (softDependencySet != null && !softDependencySet.isEmpty()) {
                if (softDependencies.containsKey(addonInfo.getId())) {
                    softDependencies.get(addonInfo.getId()).addAll(softDependencySet);
                } else {
                    softDependencies.put(addonInfo.getId(), new LinkedList<>(softDependencySet));
                }
            }

            Collection<String> dependencySet = addonInfo.getDepend();
            if (dependencySet != null && !dependencySet.isEmpty()) {
                dependencies.put(addonInfo.getId(), new LinkedList<>(dependencySet));
            }

            Collection<String> loadBeforeSet = addonInfo.getLoadBefore();
            if (loadBeforeSet != null && !loadBeforeSet.isEmpty()) {
                for (String loadBeforeTarget : loadBeforeSet) {
                    if (softDependencies.containsKey(loadBeforeTarget)) {
                        softDependencies.get(loadBeforeTarget).add(addonInfo.getId());
                    }
                    else {
                        Collection<String> shortSoftDependency = new LinkedList<>();
                        shortSoftDependency.add(addonInfo.getId());
                        softDependencies.put(loadBeforeTarget, shortSoftDependency);
                    }
                }
            }
        }

        while (!addons.isEmpty()) {
            boolean missingDependency = true;
            Iterator<String> addonIterator = addons.keySet().iterator();

            while (addonIterator.hasNext()) {
                String addon = addonIterator.next();

                if (dependencies.containsKey(addon)) {
                    Iterator<String> dependencyIterator = dependencies.get(addon).iterator();

                    while (dependencyIterator.hasNext()) {
                        String dependency = dependencyIterator.next();

                        if (loadedAddons.contains(dependency)) {
                            dependencyIterator.remove();

                        }
                        else if (!addons.containsKey(dependency)) {
                            missingDependency = false;
                            File file = addons.get(addon);
                            addonIterator.remove();
                            softDependencies.remove(addon);
                            dependencies.remove(addon);

                            MeazyMain.getLogger().log(Level.ERROR, "Couldn't load '{}' in folder '{}'",
                                    file.getPath(), directory.getPath(), new UnknownDependencyException(dependency));
                            break;
                        }
                    }

                    if (dependencies.containsKey(addon) && dependencies.get(addon).isEmpty()) {
                        dependencies.remove(addon);
                    }
                }
                if (softDependencies.containsKey(addon)) {

                    softDependencies.get(addon).removeIf(softDependency -> !addons.containsKey(softDependency));

                    if (softDependencies.get(addon).isEmpty()) {
                        softDependencies.remove(addon);
                    }
                }
                if (!(dependencies.containsKey(addon) || softDependencies.containsKey(addon)) && addons.containsKey(addon)) {
                    File file = addons.get(addon);
                    addonIterator.remove();
                    missingDependency = false;

                    try {
                        result.add(loadAddon(file));
                        loadedAddons.add(addon);
                    }
                    catch (InvalidAddonException e) {
                        MeazyMain.getLogger().log(Level.ERROR, "Couldn't load '{}' in folder '{}'", file.getPath(), directory.getPath(), e);
                    }
                }
            }

            if (missingDependency) {
                addonIterator = addons.keySet().iterator();

                while (addonIterator.hasNext()) {
                    String addon = addonIterator.next();

                    if (!dependencies.containsKey(addon)) {
                        softDependencies.remove(addon);
                        missingDependency = false;
                        File file = addons.get(addon);
                        addonIterator.remove();

                        try {
                            result.add(loadAddon(file));
                            loadedAddons.add(addon);
                            break;
                        }
                        catch (InvalidAddonException e) {
                            MeazyMain.getLogger().log(Level.ERROR, "Couldn't load '{}' in folder '{}'", file.getPath(), directory.getPath(), e);
                        }
                    }
                }

                if (missingDependency) {
                    softDependencies.clear();
                    dependencies.clear();
                    Iterator<File> failedAddonIterator = addons.values().iterator();

                    while (failedAddonIterator.hasNext()) {
                        File file = failedAddonIterator.next();
                        failedAddonIterator.remove();
                        MeazyMain.getLogger().log(Level.ERROR, "Couldn't load '{}' in folder '{}': circular dependency detected",
                                file.getPath(), directory.getPath());
                    }
                }
            }
        }

        return result.toArray(new Addon[0]);
    }

    /**
     * Loads addon from given file
     *
     * @param file Addon's file
     * @return Loaded addon
     * @throws InvalidAddonException When incorrect file is presented
     */
    public synchronized Addon loadAddon(File file) throws InvalidAddonException {
        if (file == null) throw new IllegalArgumentException("File can't be null");

        Set<Pattern> filters = fileAssociations.keySet();
        Addon result = null;

        for (Pattern filter : filters) {
            String name = file.getName();
            Matcher match = filter.matcher(name);

            if (match.find()) {
                AddonLoader loader = fileAssociations.get(filter);

                result = loader.loadAddon(file);
            }
        }

        if (result != null) {
            addons.add(result);
            lookupIds.put(result.getAddonInfo().getId(), result);
        }

        return result;
    }

    /**
     * Checks if the given addon is loaded and returns it when applicable
     * <p>
     * Id of the addon is case-sensitive
     *
     * @param id Id of the addon to check
     * @return Addon if it exists, otherwise null
     */
    public synchronized Addon getAddon(String id) {
        return lookupIds.get(id.replace(' ', '_'));
    }

    public synchronized Addon[] getAddons() {
        return addons.toArray(new Addon[0]);
    }

    /**
     * Checks if the given addon is enabled or not
     * <p>
     * Id of the addon is case-sensitive.
     *
     * @param id Id of the addon to check
     * @return True if the addon is enabled, otherwise false
     */
    public boolean isAddonEnabled(String id) {
        return isAddonEnabled(getAddon(id));
    }

    /**
     * Checks if the given addon is enabled or not
     *
     * @param addon Addon to check
     * @return True if the addon is enabled, otherwise false
     */
    public boolean isAddonEnabled(Addon addon) {
        if (addon != null && addons.contains(addon)) return addon.isEnabled();
        else return false;
    }

    /**
     * Enables given addon
     *
     * @param addon Addon to enable
     */
    public void enableAddon(Addon addon) {
        if (!addon.isEnabled()) {
            try {
                addon.getAddonLoader().enableAddon(addon);
            }
            catch (Throwable e) {
                MeazyMain.getLogger().log(Level.ERROR, "Error occurred (in the addon loader) while enabling {}", addon.getAddonInfo().getFullName(), e);
            }
        }
    }
}