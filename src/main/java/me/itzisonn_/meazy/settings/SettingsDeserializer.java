package me.itzisonn_.meazy.settings;

import com.google.gson.*;

import java.lang.reflect.Type;

public class SettingsDeserializer implements JsonDeserializer<Settings> {
    @Override
    public Settings deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("language") == null) throw new InvalidSettingsException("Settings doesn't have field language");
        String language = object.get("language").getAsString();

        if (object.get("exception_absent_key") == null) throw new InvalidSettingsException("Settings doesn't have field exception_absent_key");
        boolean exceptionAbsentKey = object.get("exception_absent_key").getAsBoolean();

        if (object.get("enable_default_addon") == null) throw new InvalidSettingsException("Settings doesn't have field enable_default_addon");
        boolean enableDefaultAddon = object.get("enable_default_addon").getAsBoolean();

        return new Settings(language, exceptionAbsentKey, enableDefaultAddon);
    }
}
