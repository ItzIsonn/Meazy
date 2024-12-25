package me.itzisonn_.meazy.parser.json_converters.expression.identifier;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;

public class ClassIdentifierConverter implements Converter<ClassIdentifier> {
    @Override
    public ClassIdentifier deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("class_identifier")) {
            if (object.get("id") == null) throw new InvalidCompiledFileException("ClassIdentifier doesn't have field id");
            String id = object.get("id").getAsString();

            return new ClassIdentifier(id);
        }

        throw new InvalidCompiledFileException("Can't deserialize ClassIdentifier because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(ClassIdentifier classIdentifier, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "class_identifier");

        result.addProperty("id", classIdentifier.getId());

        return result;
    }

    @Override
    public String getId() {
        return "class_identifier";
    }
}