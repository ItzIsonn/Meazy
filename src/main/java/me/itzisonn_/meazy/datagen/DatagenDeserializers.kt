package me.itzisonn_.meazy.datagen

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import me.itzisonn_.meazy.MeazyMain
import me.itzisonn_.meazy.lexer.NativeCanMatch
import me.itzisonn_.meazy.lexer.TokenType
import me.itzisonn_.meazy.lexer.TokenTypeSet
import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.registry.RegistryIdentifier
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
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
            val tokenTypes: MutableSet<TokenType> = HashSet()

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

            if (jsonObject.get("can_match") != null) {
                val path = jsonObject.get("can_match").asString.split("#".toRegex()).dropLastWhile { it.isEmpty() }
                val className = path[0]
                val methodName = path[1]

                var cls: Class<*>?
                try {
                    cls = Class.forName(className)
                }
                catch (e: ClassNotFoundException) {
                    throw RuntimeException(
                        "Can't find specified class $className for canMatch method in TokenType with id $id",
                        e
                    )
                }

                val method: Method?
                try {
                    method = cls.getDeclaredMethod(methodName, String::class.java)
                }
                catch (e: NoSuchMethodException) {
                    throw RuntimeException(
                        "Can't find specified method $methodName for canMatch method in TokenType with id $id",
                        e
                    )
                }

                if (!method.isAnnotationPresent(NativeCanMatch::class.java)) throw RuntimeException("Specified non-native method for canMatch method in TokenType with id $id")
                if (!method.canAccess(null)) throw RuntimeException("Specified inaccessible method for canMatch method in TokenType with id $id")
                if (!Boolean::class.javaPrimitiveType!!.isAssignableFrom(method.returnType)) throw RuntimeException(
                    "Specifier method with non-boolean return type for canMatch method in TokenType with id $id"
                )

                return@JsonDeserializer object : TokenType(id, regex, shouldSkip) {
                    override fun canMatch(string: String): Boolean {
                        try {
                            return method.invoke(null, string) as Boolean
                        }
                        catch (e: IllegalAccessException) {
                            throw RuntimeException(e)
                        }
                        catch (e: InvocationTargetException) {
                            throw RuntimeException(e)
                        }
                    }
                }
            }
            TokenType(id, regex, shouldSkip)
        }
}
