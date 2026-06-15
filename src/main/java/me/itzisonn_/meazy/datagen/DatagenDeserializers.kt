package me.itzisonn_.meazy.datagen

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import me.itzisonn_.meazy.MeazyMain
import me.itzisonn_.meazy.lexer.TokenType
import me.itzisonn_.meazy.lexer.TokenTypeSet
import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.registry.RegistryIdentifier
import java.lang.reflect.Type

/**
 * All basic datagen deserializers
 */
object DatagenDeserializers {
    /**
     * @return Deserializer for [TokenType]
     */
    val tokenTypeSetDeserializer =
        JsonDeserializer { jsonElement: JsonElement?, _: Type?, _: JsonDeserializationContext? ->
            val jsonObject = jsonElement!!.getAsJsonObject()
            if (jsonObject.get("id") == null) throw InvalidDatagenJsonException("TokenTypeSet doesn't have field id")
            val id = jsonObject.get("id").asString

            if (jsonObject.get("token_types") == null) throw InvalidDatagenJsonException("TokenTypeSet doesn't have field token_types")
            val tokenTypes = mutableSetOf<TokenType>()

            for (element in jsonObject.get("token_types").getAsJsonArray()) {
                val tokenTypeId = element.asString
                val tokenTypeEntry = try {
                    Registries.TOKEN_TYPES.getEntry(RegistryIdentifier.of(tokenTypeId))
                }
                catch (_: IllegalArgumentException) {
                    Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier(tokenTypeId))
                }

                if (tokenTypeEntry == null) throw InvalidDatagenJsonException("TokenType with id '$tokenTypeId' doesn't exist")
                tokenTypes.add(tokenTypeEntry.getValue())
            }
            TokenTypeSet(id, tokenTypes)
        }


    /**
     * @return Deserializer for [TokenType]
     */
    val tokenTypeDeserializer =
        JsonDeserializer { jsonElement: JsonElement?, _: Type?, _: JsonDeserializationContext? ->
            val jsonObject = jsonElement!!.getAsJsonObject()
            if (jsonObject.get("id") == null) throw InvalidDatagenJsonException("TokenType doesn't have field id")

            val id = jsonObject.get("id").asString
            val regex = if (jsonObject.get("regex") != null) jsonObject.get("regex").asString else null
            val shouldSkip = jsonObject.get("should_skip") != null && jsonObject.get("should_skip").asBoolean

            if (jsonObject.has("expect") && jsonObject.get("expect").asBoolean) {
                check(!jsonObject.has("regex") && !jsonObject.has("should_skip")) {
                    "Expected token type can't have neither regex nor should_skip members"
                }

                val entry = Registries.TOKEN_TYPES.getEntry(MeazyMain.getDefaultIdentifier(id))
                    ?: error("Expected registered token type with id '$id'")
                return@JsonDeserializer entry.getValue()
            }

            TokenType(id, regex, shouldSkip)
        }
}
