package me.itzisonn_.meazy.parser.json_converters.expression.identifier;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class ClassIdentifierConverter extends Converter<ClassIdentifier> {
    @Override
    public ClassIdentifier deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        if (object.get("id") == null) throw new InvalidCompiledFileException(getIdentifier(), "id");
        String id = object.get("id").getAsString();

        return new ClassIdentifier(id);
    }

    @Override
    public JsonElement serialize(ClassIdentifier classIdentifier, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.addProperty("id", classIdentifier.getId());

        return result;
    }

    @Override
    public RegistryIdentifier getIdentifier() {
        return RegistryIdentifier.ofDefault("class_identifier");
    }
}