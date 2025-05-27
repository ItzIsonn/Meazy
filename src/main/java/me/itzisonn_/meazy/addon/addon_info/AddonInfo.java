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
public final class AddonInfo {
    /**
     * Unique id
     */
    private final String id;

    /**
     * Version
     */
    private final Version version;

    /**
     * Full name of the main class that extends {@link Addon}
     */
    private final String main;

    /**
     *  Description of the addon's functionality
     */
    private final String description;

    /**
     *  List of authors
     */
    private final List<String> authors;

    /**
     * Required version of Meazy to run this addon
     */
    private final Version coreDepend;

    /**
     * List of other addons that the addon requires
     */
    private final List<String> depend;

    /**
     * List of other addons that the addon requires for full functionality
     */
    private final List<String> softDepend;

    /**
     * List of addons that should consider this addon a soft-dependency
     */
    private final List<String> loadBefore;

    public AddonInfo(String id, Version version, String main, String description, List<String> authors, Version coreDepend,
                     List<String> depend, List<String> softDepend, List<String> loadBefore) throws NullPointerException, IllegalArgumentException {
        if (id == null) throw new NullPointerException("Id can't be null");
        if (version == null) throw new NullPointerException("Version can't be null");
        if (main == null) throw new NullPointerException("Main can't be null");

        if (!id.matches(MeazyMain.IDENTIFIER_REGEX)) throw new IllegalArgumentException("Id doesn't match Identifier Regex");

        if (depend != null) {
            for (String string : depend) {
                if (!string.matches(MeazyMain.IDENTIFIER_REGEX))
                    throw new IllegalArgumentException(string + " in depend list doesn't match Identifier Regex");
            }
        }

        if (softDepend != null) {
            for (String string : softDepend) {
                if (!string.matches(MeazyMain.IDENTIFIER_REGEX))
                    throw new IllegalArgumentException(string + " in softdepend list doesn't match Identifier Regex");
            }
        }

        if (loadBefore != null) {
            for (String string : loadBefore) {
                if (!string.matches(MeazyMain.IDENTIFIER_REGEX))
                    throw new IllegalArgumentException(string + " in loadbefore list doesn't match Identifier Regex");
            }
        }

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
}