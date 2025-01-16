package me.itzisonn_.meazy.parser.json_converters;

import com.google.gson.*;
import lombok.Getter;
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
@Getter
public abstract class Converter<T extends Statement> implements JsonDeserializer<T>, JsonSerializer<T> {
    /**
     * RegistryIdentifier of this converter
     */
    private final RegistryIdentifier identifier;

    /**
     * Converter constructor
     *
     * @param identifier Converter's identifier
     *
     * @throws NullPointerException If given identifier is null
     */
    protected Converter(RegistryIdentifier identifier) {
        if (identifier == null) throw new NullPointerException("Identifier can't be null");
        this.identifier = identifier;
    }

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
}