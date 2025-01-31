package me.itzisonn_.meazy.parser.json_converters.expression.identifier;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class FunctionIdentifierConverter extends Converter<FunctionIdentifier> {
    public FunctionIdentifierConverter() {
        super(RegistryIdentifier.ofDefault("function_identifier"));
    }

    @Override
    public FunctionIdentifier deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        return new FunctionIdentifier(getElement(object, "id").getAsString());
    }

    @Override
    public JsonElement serialize(FunctionIdentifier functionIdentifier, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.addProperty("id", functionIdentifier.getId());

        return result;
    }
}