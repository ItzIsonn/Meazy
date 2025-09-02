package me.itzisonn_.meazy.addon.addon_info;

import com.google.gson.*;
import lombok.Getter;
import me.itzisonn_.meazy.FileUtils;
import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.version.Version;

import java.io.*;
import java.util.*;

/**
 * Stores information about {@link Addon}
 */
@Getter
public class AddonInfo {
    private final String id;
    private final Version version;
    private final String main;
    private final String description;
    private final List<String> authors;
    private final Version coreDepend;
    private final List<String> depend;
    private final List<String> softDepend;
    private final List<String> loadBefore;

    /**
     * @param id          Unique id
     * @param version     Version
     * @param main        Full name of the main class that extends {@link Addon}
     * @param description Description of the addon's functionality
     * @param authors     List of authors
     * @param coreDepend  Required version of Meazy to run this addon
     * @param depend      List of other addons that the addon requires
     * @param softDepend  List of other addons that the addon requires for full functionality
     * @param loadBefore  List of addons that should consider this addon a soft-dependency
     *
     * @throws NullPointerException If either id, version or main is null
     * @throws IllegalArgumentException If either id, depend, softDepend or loadBefore doesn't match Identifier Regex
     */
    public AddonInfo(String id, Version version, String main, String description, List<String> authors,
                     Version coreDepend, List<String> depend, List<String> softDepend, List<String> loadBefore) {
        if (id == null) throw new NullPointerException("Id can't be null");
        if (version == null) throw new NullPointerException("Version can't be null");
        if (main == null) throw new NullPointerException("Main can't be null");

        if (!id.matches(MeazyMain.IDENTIFIER_REGEX))
            throw new IllegalArgumentException("Id doesn't match Identifier Regex");

        String dependMismatch = matchesIdentifierRegex(depend);
        if (dependMismatch != null) throw new IllegalArgumentException(dependMismatch + " in depend list doesn't match Identifier Regex");

        String softDependMismatch = matchesIdentifierRegex(softDepend);
        if (softDependMismatch != null) throw new IllegalArgumentException(softDependMismatch + " in softdepend list doesn't match Identifier Regex");

        String loadBeforeMismatch = matchesIdentifierRegex(loadBefore);
        if (loadBeforeMismatch != null) throw new IllegalArgumentException(loadBeforeMismatch + " in softdepend list doesn't match Identifier Regex");

        this.id = id;
        this.version = version;
        this.main = main;
        this.description = description;
        this.authors = authors;
        this.coreDepend = coreDepend;
        this.depend = depend;
        this.softDepend = softDepend;
        this.loadBefore = loadBefore;
    }

    /**
     * @return String in format '{@code id} v{@code version}'
     */
    public String getFullName() {
        return id + " v" + version;
    }


    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(AddonInfo.class, new AddonInfoDeserializer())
            .create();

    /**
     * Loads addon info from given stream
     *
     * @param stream InputStream
     * @return AddonInfo
     */
    public static AddonInfo loadAddonInfo(InputStream stream) {
        return gson.fromJson(FileUtils.getLines(stream), AddonInfo.class);
    }

    private static String matchesIdentifierRegex(List<String> list) {
        if (list == null) return null;

        for (String string : list) {
            if (!string.matches(MeazyMain.IDENTIFIER_REGEX)) return string;
        }

        return null;
    }
}