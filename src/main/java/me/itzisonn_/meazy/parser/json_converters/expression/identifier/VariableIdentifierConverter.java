package me.itzisonn_.meazy.parser.json_converters.expression.identifier;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;

public class VariableIdentifierConverter implements Converter<VariableIdentifier> {
    @Override
    public VariableIdentifier deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("variable_identifier")) {
            if (object.get("id") == null) throw new InvalidCompiledFileException("VariableIdentifier doesn't have field id");
            String id = object.get("id").getAsString();

            return new VariableIdentifier(id);
        }

        throw new InvalidCompiledFileException("Can't deserialize VariableIdentifier because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(VariableIdentifier variableIdentifier, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "variable_identifier");

        result.addProperty("id", variableIdentifier.getId());

        return result;
    }

    @Override
    public String getId() {
        return "variable_identifier";
    }
}