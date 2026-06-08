package me.itzisonn_.meazy.settings

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

object SettingsDeserializer : JsonDeserializer<Settings> {
    override fun deserialize(jsonElement: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Settings {
        val jsonObject = jsonElement.getAsJsonObject()

        if (jsonObject.get("language") == null) throw InvalidSettingsException("Settings doesn't have field language")
        val language = jsonObject.get("language").asString

        if (jsonObject.get("exception_absent_key") == null) throw InvalidSettingsException("Settings doesn't have field exception_absent_key")
        val exceptionAbsentKey = jsonObject.get("exception_absent_key").asBoolean

        return Settings(language, exceptionAbsentKey)
    }
}
