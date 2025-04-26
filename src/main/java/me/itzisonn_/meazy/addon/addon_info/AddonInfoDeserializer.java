package me.itzisonn_.meazy.addon.addon_info;

import com.google.gson.*;
import me.itzisonn_.meazy.addon.InvalidAddonInfoException;
import me.itzisonn_.meazy.version.Version;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AddonInfoDeserializer implements JsonDeserializer<AddonInfo> {
    @Override
    public AddonInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String id = jsonObject.get("id").getAsString();
        String version = jsonObject.get("version").getAsString();
        String main = jsonObject.get("main").getAsString();

        String description;
        if (jsonObject.get("description") != null) description = jsonObject.get("description").getAsString();
        else description = "";

        List<String> authors;
        if (jsonObject.get("author") != null) authors = List.of(jsonObject.get("author").getAsString());
        else if (jsonObject.get("authors") != null) authors = jsonObject.get("authors").getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList();
        else authors = new ArrayList<>();

        Version coreDepend;
        if (jsonObject.get("core_depend") != null) coreDepend = Version.of(jsonObject.get("core_depend").getAsString());
        else coreDepend = null;

        List<String> depend;
        if (jsonObject.get("depend") != null) depend = jsonObject.get("depend").getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList();
        else depend = new ArrayList<>();

        List<String> softDepend;
        if (jsonObject.get("softDepend") != null) softDepend = jsonObject.get("softDepend").getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList();
        else softDepend = new ArrayList<>();

        List<String> loadBefore;
        if (jsonObject.get("loadBefore") != null) loadBefore = jsonObject.get("loadBefore").getAsJsonArray().asList().stream().map(JsonElement::getAsString).toList();
        else loadBefore = new ArrayList<>();

        try {
            return new AddonInfo(id, Version.of(version), main, description, authors, coreDepend, depend, softDepend, loadBefore);
        }
        catch (InvalidAddonInfoException e) {
            throw new RuntimeException(e);
        }
    }
}
