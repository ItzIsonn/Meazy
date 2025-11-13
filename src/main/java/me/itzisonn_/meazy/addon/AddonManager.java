package me.itzisonn_.meazy.addon;

import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.addon.datagen.DatagenDeserializers;
import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.lexer.TokenType;
import me.itzisonn_.meazy.lexer.TokenTypeSet;
import me.itzisonn_.meazy.logging.LogLevel;
import me.itzisonn_.registry.RegistryIdentifier;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Represents an AddonManager
 */
public final class AddonManager {
    private static final Pattern FILE_FILTER = Pattern.compile(".+\\.jar$");
    public static final File ADDONS_FOLDER;

    static {
        try {
            ADDONS_FOLDER = new File(new File(MeazyMain.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent() + "/addons/");
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(Text.translatable("meazy:addons.cant_load_folder").toString(), e);
        }

        if (!ADDONS_FOLDER.exists() && !ADDONS_FOLDER.mkdirs()) throw new RuntimeException(Text.translatable("meazy:addons.cant_load_folder").toString());
    }

    private final AddonLoader addonLoader = new AddonLoader();
    private final List<Addon> addons = new ArrayList<>();



    /**
     * Loads addons
     */
    public void loadAddons() {
        Map<String, File> addons = new HashMap<>();
        Set<String> loadedAddons = new HashSet<>();
        Map<String, Collection<String>> dependencies = new HashMap<>();
        Map<String, Collection<String>> softDependencies = new HashMap<>();

        File[] listFiles = ADDONS_FOLDER.listFiles();
        if (listFiles == null) throw new NullPointerException("AddonsFolder's list of files is null");

        for (File file : listFiles) {
            if (!FILE_FILTER.matcher(file.getName()).matches()) continue;

            AddonInfo addonInfo;
            try {
                addonInfo = addonLoader.getAddonInfo(file);

                if (addonInfo.getCoreDepend() != null && !addonInfo.getCoreDepend().equals(MeazyMain.VERSION)) {
                    MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:addons.cant_load.unsupported_version", file.getPath(), addonInfo.getCoreDepend()));
                    continue;
                }
            }
            catch (InvalidAddonInfoException e) {
                MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:addons.cant_load.general", file.getPath(), e));
                continue;
            }

            File replacedFile = addons.put(addonInfo.getId(), file);
            if (replacedFile != null) {
                MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:addons.duplicate_name", addonInfo.getId(), file.getPath(), replacedFile.getPath()));
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

                            MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:addons.cant_load.general", file.getPath(), new UnknownDependencyException(dependency)));
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
                        loadAddon(file);
                        loadedAddons.add(addon);
                    }
                    catch (InvalidAddonException e) {
                        MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:addons.cant_load.general", file.getPath(), e));
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
                            loadAddon(file);
                            loadedAddons.add(addon);
                            break;
                        }
                        catch (InvalidAddonException e) {
                            MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:addons.cant_load.general", file.getPath(), e));
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
                        MeazyMain.LOGGER.log(LogLevel.ERROR, Text.translatable("meazy:addons.cant_load.circular_dependency", file.getPath()));
                    }
                }
            }
        }
    }

    /**
     * Loads addon from given file
     * @param file Addon's file
     *
     * @throws NullPointerException If given file is null
     * @throws InvalidAddonException If given file is invalid
     */
    private void loadAddon(File file) throws NullPointerException, InvalidAddonException {
        if (file == null) throw new NullPointerException("File can't be null");
        if (!FILE_FILTER.matcher(file.getName()).matches()) throw new InvalidAddonException("Invalid addon file");
        addons.add(addonLoader.loadAddon(file));
    }



    /**
     * Enables addons
     */
    public void enableAddons() {
        if (MeazyMain.SETTINGS_MANAGER.getSettings().isEnableDefaultAddon()) loadDefaultAddon();
        loadAddons();

        for (Addon addon : getAddons()) {
            if (addon.getLanguageFileProvider() != null) MeazyMain.BUNDLE_MANAGER.addLanguageFileProvider(addon.getLanguageFileProvider());
            enableAddon(addon);

            for (TokenType tokenType : addon.getDatagenManager().getDeserializedMultiple("token_type", TokenType.class, DatagenDeserializers.getTokenTypeDeserializer())) {
                Registries.TOKEN_TYPES.register(RegistryIdentifier.of(addon.getAddonInfo().getId(), tokenType.getId()), tokenType);
            }

            for (TokenTypeSet tokenTypeSet : addon.getDatagenManager().getDeserializedSingle("token_type_set", TokenTypeSet.class, DatagenDeserializers.getTokenTypeSetDeserializer(addon))) {
                Registries.TOKEN_TYPE_SETS.register(RegistryIdentifier.of(addon.getAddonInfo().getId(), tokenTypeSet.getId()), tokenTypeSet);
            }
        }

        int addons = getAddons().size();
        if (addons == 1) MeazyMain.LOGGER.log(LogLevel.INFO, Text.translatable("meazy:addons.single_loaded"));
        else MeazyMain.LOGGER.log(LogLevel.INFO, Text.translatable("meazy:addons.multiple_loaded", addons));
    }

    private void loadDefaultAddon() {
        String fileName = "MeazyAddon-v" + MeazyMain.VERSION + ".jar";

        File addonFile;
        try {
            addonFile = new File(ADDONS_FOLDER.getAbsolutePath() + "/" + fileName);

            if (addonFile.exists()) return;
            if (!addonFile.createNewFile()) throw new RuntimeException(Text.translatable("meazy:addons.cant_load_file").toString());
        }
        catch (IOException e) {
            throw new RuntimeException(Text.translatable("meazy:addons.cant_load_file").toString(), e);
        }

        InputStream in = MeazyMain.class.getClassLoader().getResourceAsStream(fileName);
        if (in == null) throw new RuntimeException(Text.translatable("meazy:addons.default.cant_find_file").toString());

        try {
            OutputStream out = new FileOutputStream(addonFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            out.close();
            in.close();
        }
        catch (IOException e) {
            throw new RuntimeException(Text.translatable("meazy:addons.cant_create_file").toString(), e);
        }
    }



    /**
     * Returns loaded addon with given id
     * @param id Id of the addon to check
     *
     * @return Addon if it exists, otherwise null
     * @throws NullPointerException If given id is null
     */
    public Addon getAddon(String id) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");

        for (Addon addon : addons) {
            if (addon.getAddonInfo().getId().equals(id)) return addon;
        }

        return null;
    }

    /**
     * @return List of loaded addons
     */
    public List<Addon> getAddons() {
        return List.copyOf(addons);
    }

    /**
     * Checks whether the addon with given id
     *
     * @param id Id of the addon
     * @return Whether the addon exists and is enabled
     *
     * @throws NullPointerException If given id is null
     */
    public boolean isAddonEnabled(String id) {
        if (id == null) throw new NullPointerException("Id can't be null");
        Addon addon = getAddon(id);

        if (addon == null) return false;
        return addon.isEnabled();
    }

    /**
     * Enables given addon
     * @param addon Addon to enable
     *
     * @throws NullPointerException If given addon is null
     * @throws IllegalStateException If given addon has already been enabled
     */
    public void enableAddon(Addon addon) throws NullPointerException, IllegalStateException {
        addonLoader.enableAddon(addon);
    }



    public Class<?> getClassByName(String name) {
        return addonLoader.getClassByName(name, null);
    }
}