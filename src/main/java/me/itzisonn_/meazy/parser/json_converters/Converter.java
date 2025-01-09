package me.itzisonn_.meazy.parser.json_converters;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.ParameterizedType;

/**
 * Json object converter
 *
 * @param <T> Converted Statement's type
 *
 * @see Registries#CONVERTERS
 */
public abstract class Converter<T extends Statement> implements JsonDeserializer<T>, JsonSerializer<T> {
    /**
     * Checks type of given JsonObject
     *
     * @param object JsonObject to check
     *
     * @throws InvalidCompiledFileException If JsonObject doesn't contain member {@code type} or it's value doesn't match this converter's identifier
     */
    @SuppressWarnings("unchecked")
    protected final void checkType(JsonObject object) throws InvalidCompiledFileException {
        if (object.get("type") == null || !object.get("type").getAsString().equals(getIdentifier().toString())) {
            throw new InvalidCompiledFileException("Can't deserialize " + ((Class<? extends Statement>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getSimpleName() + " because specified type is null or doesn't match");
        }
    }

    /**
     * @return JsonObject with property {@code type} set to this converter's identifier
     */
    protected final JsonObject getJsonObject() {
        JsonObject result = new JsonObject();
        result.addProperty("type", getIdentifier().toString());

        return result;
    }

    /**
     * @return RegistryIdentifier of this converter
     */
    public abstract RegistryIdentifier getIdentifier();
}