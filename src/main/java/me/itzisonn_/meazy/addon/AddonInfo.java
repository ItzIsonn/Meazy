package me.itzisonn_.meazy.addon;

import com.google.gson.*;
import lombok.Getter;
import me.itzisonn_.meazy.FileUtils;
import me.itzisonn_.meazy.MeazyMain;
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
    private final String className;
    private final String description;
    private final List<String> authors;
    private final Version coreDepend;
    private final List<String> depend;
    private final List<String> softDepend;
    private final List<String> loadBefore;

    /**
     * Main constructor
     *
     * @param id          Unique id
     * @param version     Version
     * @param className   Full name of the main class that extends {@link Addon}
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
    public AddonInfo(String id, Version version, String className, String description, List<String> authors, Version coreDepend,
                     List<String> depend, List<String> softDepend, List<String> loadBefore) throws NullPointerException, IllegalArgumentException {
        if (id == null) throw new NullPointerException("Id can't be null");
        if (version == null) throw new NullPointerException("Version can't be null");
        if (className == null) throw new NullPointerException("Main can't be null");

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
        this.className = className;
        this.description = description == null ? "" : description;
        this.authors = authors == null ? List.of() : List.copyOf(authors);
        this.coreDepend = coreDepend;
        this.depend = depend == null ? List.of() : List.copyOf(depend);
        this.softDepend = softDepend == null ? List.of() : List.copyOf(softDepend);
        this.loadBefore = loadBefore == null ? List.of() : List.copyOf(loadBefore);
    }

    /**
     * Constructor that uses given inputStream to get info
     *
     * @param inputStream InputStream
     *
     * @throws NullPointerException If given inputStream is null
     * @throws InvalidAddonInfoException If
     */
    public AddonInfo(InputStream inputStream) throws NullPointerException, InvalidAddonInfoException {
        if (inputStream == null) throw new NullPointerException("InputStream can't be null");
        JsonElement jsonElement = JsonParser.parseString(FileUtils.getLines(inputStream));

        if (!jsonElement.isJsonObject()) throw new InvalidAddonInfoException("AddonInfo must be object");
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        JsonElement idElement = jsonObject.get("id");
        if (idElement == null || !idElement.isJsonPrimitive()) throw new InvalidAddonInfoException("Addon id must be string");
        String id = idElement.getAsString();

        JsonElement versionElement = jsonObject.get("version");
        if (versionElement == null || !versionElement.isJsonPrimitive()) throw new InvalidAddonInfoException("Addon version must be string");
        String version = versionElement.getAsString();

        JsonElement classNameElement = jsonObject.get("class_name");
        if (classNameElement == null || !classNameElement.isJsonPrimitive()) throw new InvalidAddonInfoException("Addon class name must be string");
        String className = classNameElement.getAsString();

        JsonElement descriptionElement = jsonObject.get("description");
        String description = null;
        if (descriptionElement != null && descriptionElement.isJsonPrimitive()) description = jsonObject.get("description").getAsString();

        List<String> authors;
        if (jsonObject.get("author") != null) authors = List.of(jsonObject.get("author").getAsString());
        else if (jsonObject.get("authors") != null) authors = jsonObject.get("authors").getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList();
        else authors = new ArrayList<>();

        JsonElement coreDependElement = jsonObject.get("core_depend");
        Version coreDepend = null;
        if (coreDependElement != null && coreDependElement.isJsonPrimitive()) coreDepend = Version.of(coreDependElement.getAsString());

        JsonElement dependElement = jsonObject.get("depend");
        List<String> depend = null;
        if (dependElement != null && dependElement.isJsonArray()) depend = dependElement.getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList();

        JsonElement softDependElement = jsonObject.get("soft_depend");
        List<String> softDepend = null;
        if (softDependElement != null && softDependElement.isJsonArray()) softDepend = softDependElement.getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList();

        JsonElement loadBeforeElement = jsonObject.get("load_before");
        List<String> loadBefore = null;
        if (loadBeforeElement != null && loadBeforeElement.isJsonArray()) loadBefore = loadBeforeElement.getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList();

        this(id, Version.of(version), className, description, authors, coreDepend, depend, softDepend, loadBefore);
    }

    /**
     * @return String in format '{@code id} v{@code version}'
     */
    public String getFullName() {
        return id + " v" + version;
    }



    private static String matchesIdentifierRegex(List<String> list) {
        if (list == null) return null;

        for (String string : list) {
            if (!string.matches(MeazyMain.IDENTIFIER_REGEX)) return string;
        }

        return null;
    }
}