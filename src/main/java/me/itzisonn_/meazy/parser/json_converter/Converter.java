package me.itzisonn_.meazy.parser.json_converter;

import com.google.gson.*;
import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.registry.RegistryIdentifier;

import java.lang.reflect.ParameterizedType;

/**
 * Represents json object converter
 *
 * @param <T> Converted statement's type
 * @see Registries#CONVERTERS
 */
@Getter
public abstract class Converter<T extends Statement> implements JsonDeserializer<T>, JsonSerializer<T> {
    /**
     * Registry identifier
     */
    private final RegistryIdentifier id;

    /**
     * @param id Registry identifier
     * @throws NullPointerException If given id is null
     */
    protected Converter(RegistryIdentifier id) {
        if (id == null) throw new NullPointerException("Id can't be null");
        this.id = id;
    }

    /**
     * Checks type of given object
     *
     * @param object JsonObject to check
     * @throws InvalidCompiledFileException If given object doesn't contain member {@code type} or it's value doesn't match this converter's id
     */
    @SuppressWarnings("unchecked")
    protected final void checkType(JsonObject object) throws InvalidCompiledFileException {
        if (object.get("type") == null || !object.get("type").getAsString().equals(getId().toString())) {
            throw new InvalidCompiledFileException("Can't deserialize " +
                    ((Class<? extends Statement>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getSimpleName() +
                    " because specified type is null or doesn't match");
        }
    }

    /**
     * @return {@link JsonObject} with property {@code type} set to this converter's id
     */
    protected final JsonObject getJsonObject() {
        JsonObject result = new JsonObject();
        result.addProperty("type", getId().toString());

        return result;
    }

    /**
     * @param object JsonObject
     * @param id Id of JsonElement
     * @return JsonElement of given object with given id
     *
     * @throws InvalidCompiledFileException If JsonElement with given id doesn't exist
     */
    protected JsonElement getElement(JsonObject object, String id) throws InvalidCompiledFileException {
        return getElement(object, id, id);
    }

    /**
     * @param object JsonObject
     * @param id Id of JsonElement
     * @param exceptionId Id that will be used in InvalidCompiledFileException
     * @return JsonElement of given object with given id
     *
     * @throws InvalidCompiledFileException If JsonElement with given id doesn't exist
     */
    protected JsonElement getElement(JsonObject object, String id, String exceptionId) throws InvalidCompiledFileException {
        if (object.get(id) == null) throw new InvalidCompiledFileException(getId(), exceptionId);
        return object.get(id);
    }
}