package me.itzisonn_.meazy.addon.addon_info;

import com.google.gson.*;
import lombok.Getter;
import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.addon.AddonManager;
import me.itzisonn_.meazy.addon.InvalidAddonInfoException;
import me.itzisonn_.meazy.version.Version;

import java.io.*;
import java.util.*;

/**
 * This type is the runtime-container for the information in the addon.json.
 * All addons must contain addon.json. This file must be in the root of the jar file.
 * <p>
 * When Meazy loads an addon, it needs to know some basic information about
 * it. It reads this information from a JSON file 'addon.json'.
 * <p>
 * Almost every method corresponds with a specific entry in the
 * addon.json. These are the <b>required</b> entries for every addon.json:
 * <ul>
 * <li>{@link #getId()} - <code>name</code>
 * <li>{@link #getVersion()} - <code>version</code>
 * <li>{@link #getMain()} - <code>main</code>
 * </ul>
 * <p>
 * Failing to find at least one of these items will throw an exception and
 * cause Meazy to ignore your addon.
 * <p>
 * This is a list of the possible json keys, with specific details included in
 * the respective method documentations:
 * <table border=1>
 * <tr>
 *     <th>Node</th>
 *     <th>Method</th>
 *     <th>Summary</th>
 * </tr><tr>
 *     <td><code>id</code></td>
 *     <td>{@link #getId()}</td>
 *     <td>Addon's unique id</td>
 * </tr><tr>
 *     <td><code>version</code></td>
 *     <td>{@link #getVersion()}</td>
 *     <td>Addon's version</td>
 * </tr><tr>
 *     <td><code>main</code></td>
 *     <td>{@link #getMain()}</td>
 *     <td>The addon's initial class file</td>
 * </tr><tr>
 *     <td><code>description</code></td>
 *     <td>{@link #getDescription()}</td>
 *     <td>Addon summary</td>
 * </tr><tr>
 *     <td><code>author</code><br><code>authors</code></td>
 *     <td>{@link #getAuthors()}</td>
 *     <td>Addon's authors</td>
 *</tr><tr>
 *     <td><code>depend</code></td>
 *     <td>{@link #getDepend()}</td>
 *     <td>Other required addons</td>
 * </tr><tr>
 *     <td><code>softDepend</code></td>
 *     <td>{@link #getSoftDepend()}</td>
 *     <td>Other optional addons that add functionality</td>
 * </tr><tr>
 *     <td><code>loadBefore</code></td>
 *     <td>{@link #getLoadBefore()}</td>
 *     <td>The inverse softDepend</td>
 * </tr>
 * </table>
 * <p>
 * An addon.json example:<blockquote><pre>
 *{
 *  "id": "example_addon",
 *  "version": "1.52.0",
 *  "main": "org.example.addon.MyAddon",
 *  "description": "Example addon for Meazy programming language",
 *  "author": [
 *    "CoolProgrammer"
 *  ]
 *}
 *</pre></blockquote>
 */
@Getter
public final class AddonInfo {
    /**
     *  Id of the addon. This id is a unique identifier for addons.
     *  <ul>
     *  <li>Must match {@link MeazyMain#IDENTIFIER_REGEX}
     *  <li>Used to determine the name of the addon's data folder. Data
     *      folders are placed in the ./addons/ directory and
     *      should be used to reference the data folder
     *  <li>It is good practice to name your jar the same as this, for example
     *      'MyAddon.jar'
     *  <li>Case sensitive
     *  </ul>
     *  <p>
     *  In the addon.json, this entry is named <code>id</code>.
     *  <p>
     *  Example: <blockquote><pre>"id": "example_addon"</pre></blockquote>
     */
    private final String id;

    /**
     *  Version of the addon.
     *  <p>
     *  In the addon.json, this entry is named <code>version</code>.
     *  <p>
     *  Example: <blockquote><pre>"version": "1.52.0"</pre></blockquote>
     */
    private final Version version;

    /**
     *  Fully qualified name of the main class for a addon. The
     *  format should follow the syntax to successfully be resolved at runtime.
     *  This is the class that extends {@link Addon}.
     *  <ul>
     *  <li>This must contain the full namespace including the class file itself.
     *  <li>If your namespace is <code>org.example.addon</code>, and your class
     *      file is called <code>MyAddon</code> then this must be
     *      <code>org.example.addon.MyAddon</code>
     *  <li>No addon can use <code>org.bukkit.</code> as a base package for
     *      <b>any class</b>, including the main class.
     *  </ul>
     *  <p>
     *  In the addon.json, this entry is named <code>main</code>.
     *  <p>
     *  Example: <blockquote><pre>"main": "org.example.addon.MyAddon"</pre></blockquote>
     */
    private final String main;
    /**
     *  A human-friendly description of the functionality the addon provides.
     *  <p>
     *  In the addon.json, this entry is named <code>description</code>.
     *  <p>
     *  Example: <blockquote><pre>"description": "Example addon for Meazy programming language"</pre></blockquote>
     */
    private final String description;

    /**
     *  List of authors for the addon. Gives credit to the developer.
     *  <p>
     *  In the addon.json, this has two entries, <code>author</code> and
     *  <code>authors</code>.
     *  <p>
     *  Single author example: <blockquote><pre>"author": "CoolProgrammer"</pre></blockquote>
     *  <p>
     *  Multiple author example: <blockquote><pre>"authors": ["CoolProgrammer", "HisBestFriend", "UnknownHero777"]</pre></blockquote>
     *  <p>
     *  When both are specified, only <code>author</code> will be in the list
     */
    private final List<String> authors;

    /**
     *  Gives a list of other addons that the addon requires.
     *  <ul>
     *  <li>Use the value in the {@link #getId()} of the target addon to specify the dependency.
     *  <li>If at least one addon listed here is not found, your addon will fail to load at startup.
     *  <li>If multiple addons list each other in <code>depend</code>, all addons in this network will fail.
     *  </ul>
     *  <p>
     *  In the addon.json, this entry is named <code>depend</code>.
     *  <p>
     *  Example: <blockquote><pre>"depend": ["OneAddon", "AnotherAddon"]</pre></blockquote>
     */
    private final List<String> depend;

    /**
     *  List of other addons that the addon requires for full functionality.
     *  The {@link AddonManager} will make best effort to treat all
     *  entries here as if they were a {@link #getDepend() dependency},
     *  but will never fail because of one of these entries.
     *  <ul>
     *  <li>Use the value in the {@link #getId()} of the target addon to specify the dependency.
     *  <li>When an unresolvable addon is listed, it will be ignored and doesn't affect load order.
     *  <li>When a circular dependency occurs (a network of addons depending or soft-depending each other),
     *      it will randomly choose an addon that can be resolved when ignoring soft-dependencies.
     *  </ul>
     *  <p>
     *  In the addon.json, this entry is named <code>softdepend</code>.
     *  <p>
     *  Example: <blockquote><pre>"softdepend": ["OneAddon", "AnotherAddon"]</pre></blockquote>
     */
    private final List<String> softDepend;

    /**
     *  List of addons that should consider this addon a soft-dependency.
     *  <ul>
     *  <li>Use the value in the {@link #getId()} of the target addon to specify the dependency.
     *  <li>The addon should load before any other addons listed here.
     *  <li>Specifying another addon here is strictly equivalent to having the specified
     *      addon's {@link #getSoftDepend()} include {@link #getId()} of this addon.
     *  </ul>
     *  <p>
     *  In the addon.json, this entry is named <code>loadbefore</code>.
     *  <p>
     *  Example: <blockquote><pre>"loadbefore": ["OneAddon", "AnotherAddon"]</pre></blockquote>
     */
    private final List<String> loadBefore;

    /**
     * Returns the name of a addon, including the version.
     * This method uses the {@link #getId()} and {@link #getVersion()} entries.
     *
     * @return Id and version of the addon
     */
    public String getFullName() {
        return id + " v" + version;
    }

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(AddonInfo.class, new AddonInfoDeserializer())
            .create();

    public AddonInfo(String id, Version version, String main, String description, List<String> authors,
                     List<String> depend, List<String> softDepend, List<String> loadBefore) throws InvalidAddonInfoException {
        if (!id.matches(MeazyMain.IDENTIFIER_REGEX)) throw new InvalidAddonInfoException("Id doesn't match Identifier Regex");
        for (String string : depend) {
            if (!string.matches(MeazyMain.IDENTIFIER_REGEX))
                throw new InvalidAddonInfoException(string + " in depend list doesn't match Identifier Regex");
        }
        for (String string : softDepend) {
            if (!string.matches(MeazyMain.IDENTIFIER_REGEX))
                throw new InvalidAddonInfoException(string + " in softdepend list doesn't match Identifier Regex");
        }
        for (String string : loadBefore) {
            if (!string.matches(MeazyMain.IDENTIFIER_REGEX))
                throw new InvalidAddonInfoException(string + " in loadbefore list doesn't match Identifier Regex");
        }

        this.id = id;
        this.version = version;
        this.main = main;
        this.description = description;
        this.authors = authors;
        this.depend = depend;
        this.softDepend = softDepend;
        this.loadBefore = loadBefore;
    }

    public static AddonInfo loadAddonInfo(InputStream stream) {
        StringBuilder json = new StringBuilder();
        for (String line : new BufferedReader(new InputStreamReader(stream)).lines().toList()) {
            json.append(line);
        }
        return gson.fromJson(json.toString(), AddonInfo.class);
    }
}