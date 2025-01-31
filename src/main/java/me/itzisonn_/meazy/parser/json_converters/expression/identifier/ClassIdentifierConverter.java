package me.itzisonn_.meazy.parser.json_converters.expression.identifier;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class ClassIdentifierConverter extends Converter<ClassIdentifier> {
    public ClassIdentifierConverter() {
        super(RegistryIdentifier.ofDefault("class_identifier"));
    }

    @Override
    public ClassIdentifier deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        return new ClassIdentifier(getElement(object, "id").getAsString());
    }

    @Override
    public JsonElement serialize(ClassIdentifier classIdentifier, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.addProperty("id", classIdentifier.getId());

        return result;
    }
}