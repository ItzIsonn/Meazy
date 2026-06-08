package me.itzisonn_.meazy.addon

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import me.itzisonn_.meazy.MeazyMain
import me.itzisonn_.meazy.util.FileUtils.getLines
import me.itzisonn_.meazy.version.Version
import me.itzisonn_.meazy.version.Version.Companion.of
import org.jspecify.annotations.NullMarked
import java.io.InputStream
import java.util.List

/**
 * Stores information about [Addon]
 */
@NullMarked
class AddonInfo {
    val id: String
    val version: Version
    val className: String
    val description: String
    val authors: MutableList<String>
    val coreDepend: Version?
    val depend: MutableList<String>
    val softDepend: MutableList<String>
    val loadBefore: MutableList<String>

    /**
     * Main constructor
     * 
     * @param id          Unique id
     * @param version     Version
     * @param className   Full name of the main class that extends [Addon]
     * @param description Description of the addon's functionality
     * @param authors     List of authors
     * @param coreDepend  Required version of Meazy to run this addon
     * @param depend      List of other addons that the addon requires
     * @param softDepend  List of other addons that the addon requires for full functionality
     * @param loadBefore  List of addons that should consider this addon a soft-dependency
     * @throws IllegalArgumentException If either id, depend, softDepend or loadBefore doesn't match Identifier Regex
     */
    constructor(
        id: String,
        version: Version,
        className: String,
        description: String?,
        authors: MutableList<String>?,
        coreDepend: Version?,
        depend: MutableList<String>?,
        softDepend: MutableList<String>?,
        loadBefore: MutableList<String>?
    ) {
        require(id.matches(MeazyMain.IDENTIFIER_REGEX.toRegex())) { "Id doesn't match Identifier Regex" }

        val dependMismatch: String? = matchesIdentifierRegex(depend)
        require(dependMismatch == null) { dependMismatch + " in depend list doesn't match Identifier Regex" }

        val softDependMismatch: String? = matchesIdentifierRegex(softDepend)
        require(softDependMismatch == null) { softDependMismatch + " in softdepend list doesn't match Identifier Regex" }

        val loadBeforeMismatch: String? = matchesIdentifierRegex(loadBefore)
        require(loadBeforeMismatch == null) { loadBeforeMismatch + " in softdepend list doesn't match Identifier Regex" }

        this.id = id
        this.version = version
        this.className = className
        this.description = description ?: ""
        this.authors = if (authors == null) mutableListOf() else List.copyOf<String>(authors)
        this.coreDepend = coreDepend
        this.depend = if (depend == null) mutableListOf() else List.copyOf<String>(depend)
        this.softDepend = if (softDepend == null) mutableListOf() else List.copyOf<String>(softDepend)
        this.loadBefore = if (loadBefore == null) mutableListOf() else List.copyOf<String>(loadBefore)
    }

    val fullName: String
        /**
         * @return String in format '`id` v`version`'
         */
        get() = id + " v" + version


    companion object {
        private fun matchesIdentifierRegex(list: MutableList<String>?): String? {
            if (list == null) return null

            for (string in list) {
                if (!string.matches(MeazyMain.IDENTIFIER_REGEX.toRegex())) return string
            }

            return null
        }
    }
}

/**
 * Constructor that uses given inputStream to get info
 *
 * @param inputStream InputStream
 * @throws InvalidAddonInfoException If
 */
fun AddonInfo(inputStream: InputStream): AddonInfo {
    val jsonElement = JsonParser.parseString(getLines(inputStream))

    if (!jsonElement.isJsonObject()) throw InvalidAddonInfoException("AddonInfo must be object")
    val jsonObject = jsonElement.getAsJsonObject()

    val idElement = jsonObject.get("id")
    if (idElement == null || !idElement.isJsonPrimitive()) throw InvalidAddonInfoException("Addon id must be string")
    val id = idElement.getAsString()

    val versionElement = jsonObject.get("version")
    if (versionElement == null || !versionElement.isJsonPrimitive()) throw InvalidAddonInfoException("Addon version must be string")
    val version = versionElement.getAsString()

    val classNameElement = jsonObject.get("class_name")
    if (classNameElement == null || !classNameElement.isJsonPrimitive()) throw InvalidAddonInfoException("Addon class name must be string")
    val className = classNameElement.getAsString()

    val descriptionElement = jsonObject.get("description")
    var description: String? = null
    if (descriptionElement != null && descriptionElement.isJsonPrimitive()) description =
        jsonObject.get("description").getAsString()

    val authors: MutableList<String>?
    if (jsonObject.get("author") != null) authors = List.of<String>(jsonObject.get("author").getAsString())
    else if (jsonObject.get("authors") != null) authors =
        jsonObject.get("authors").getAsJsonArray().asList().stream()
            .map<String> { obj: JsonElement? -> obj!!.getAsString() }.toList()
    else authors = ArrayList<String>()

    val coreDependElement = jsonObject.get("core_depend")
    var coreDepend: Version? = null
    if (coreDependElement != null && coreDependElement.isJsonPrimitive()) coreDepend =
        of(coreDependElement.getAsString())

    val dependElement = jsonObject.get("depend")
    var depend: MutableList<String>? = null
    if (dependElement != null && dependElement.isJsonArray()) depend =
        dependElement.getAsJsonArray().asList().stream().map<String> { obj: JsonElement? -> obj!!.getAsString() }
            .toList()

    val softDependElement = jsonObject.get("soft_depend")
    var softDepend: MutableList<String>? = null
    if (softDependElement != null && softDependElement.isJsonArray()) softDepend =
        softDependElement.getAsJsonArray().asList().stream()
            .map<String> { obj: JsonElement? -> obj!!.getAsString() }.toList()

    val loadBeforeElement = jsonObject.get("load_before")
    var loadBefore: MutableList<String>? = null
    if (loadBeforeElement != null && loadBeforeElement.isJsonArray()) loadBefore =
        loadBeforeElement.getAsJsonArray().asList().stream()
            .map { obj: JsonElement? -> obj!!.getAsString() }.toList()

    return AddonInfo(id, of(version), className, description, authors, coreDepend, depend, softDepend, loadBefore)
}