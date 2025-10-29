package me.itzisonn_.meazy.addon.datagen;

import com.google.gson.*;
import me.itzisonn_.meazy.MeazyMain;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.addon.Addon;
import me.itzisonn_.meazy.lexer.NativeCanMatch;
import me.itzisonn_.meazy.lexer.TokenType;
import me.itzisonn_.meazy.lexer.TokenTypeSet;
import me.itzisonn_.registry.RegistryEntry;
import me.itzisonn_.registry.RegistryIdentifier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * All basic datagen deserializers
 */
public final class DatagenDeserializers {
    private DatagenDeserializers() {}

    /**
     * @return Deserializer for {@link TokenType}
     */
    public static JsonDeserializer<TokenType> getTokenTypeDeserializer() {
        return TOKEN_TYPE;
    }

    /**
     * @return Deserializer for {@link TokenType}
     */
    public static JsonDeserializer<TokenTypeSet> getTokenTypeSetDeserializer(Addon addon) {
        return (jsonElement, _, _) -> {
            JsonObject object = jsonElement.getAsJsonObject();

            if (object.get("id") == null) throw new InvalidDatagenJsonException("TokenTypeSet doesn't have field id");
            String id = object.get("id").getAsString();

            if (object.get("token_types") == null) throw new InvalidDatagenJsonException("TokenTypeSet doesn't have field token_types");
            Set<TokenType> tokenTypes = new HashSet<>();

            for (JsonElement element : object.get("token_types").getAsJsonArray()) {
                String tokenTypeId = element.getAsString();

                RegistryEntry<TokenType> tokenTypeEntry;
                try {
                    tokenTypeEntry = Registries.TOKEN_TYPES.getEntry(RegistryIdentifier.of(tokenTypeId));
                }
                catch (IllegalArgumentException e) {
                    tokenTypeEntry = Registries.TOKEN_TYPES.getEntry(RegistryIdentifier.of(addon.getAddonInfo().getId(), tokenTypeId));
                }

                if (tokenTypeEntry == null) throw new InvalidDatagenJsonException("TokenType with id " + tokenTypeId + " doesn't exist");
                tokenTypes.add(tokenTypeEntry.getValue());
            }

            return new TokenTypeSet(id, tokenTypes);
        };
    }



    private static final JsonDeserializer<TokenType> TOKEN_TYPE = (jsonElement, _, _) -> {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("id") == null) throw new InvalidDatagenJsonException("TokenType doesn't have field id");
        String id = object.get("id").getAsString();

        if (object.get("regex") == null) throw new InvalidDatagenJsonException("TokenType doesn't have field regex");
        String regex = object.get("regex").getAsString();

        boolean shouldSkip = false;
        if (object.get("should_skip") != null) shouldSkip = object.get("should_skip").getAsBoolean();

        if (object.get("can_match") != null) {
            String[] path = object.get("can_match").getAsString().split("#");
            String className = path[0];
            String methodName = path[1];

            Class<?> cls;
            try {
                cls = Class.forName(className);
            }
            catch (ClassNotFoundException e) {
                cls = MeazyMain.ADDON_MANAGER.getClassByName(className);
                if (cls == null) throw new RuntimeException("Can't find specified class " + className + " for canMatch method in TokenType with id " + id, e);
            }

            Method method;
            try {
                method = cls.getDeclaredMethod(methodName, String.class);
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException("Can't find specified method " + methodName + " for canMatch method in TokenType with id " + id, e);
            }

            if (!method.isAnnotationPresent(NativeCanMatch.class)) throw new RuntimeException("Specified non-native method for canMatch method in TokenType with id " + id);
            if (!method.canAccess(null)) throw new RuntimeException("Specified inaccessible method for canMatch method in TokenType with id " + id);
            if (!boolean.class.isAssignableFrom(method.getReturnType())) throw new RuntimeException("Specifier method with non-boolean return type for canMatch method in TokenType with id " + id);

            return new TokenType(id, regex, shouldSkip) {
                @Override
                public boolean canMatch(String string) {
                    try {
                        return (boolean) method.invoke(null, string);
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }

        return new TokenType(id, regex, shouldSkip);
    };
}
