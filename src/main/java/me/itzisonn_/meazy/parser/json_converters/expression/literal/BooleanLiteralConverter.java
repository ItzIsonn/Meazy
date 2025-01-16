package me.itzisonn_.meazy.parser.json_converters.expression.literal;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.literal.BooleanLiteral;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class BooleanLiteralConverter extends Converter<BooleanLiteral> {
    public BooleanLiteralConverter() {
        super(RegistryIdentifier.ofDefault("boolean_literal"));
    }

    @Override
    public BooleanLiteral deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        if (object.get("value") == null) throw new InvalidCompiledFileException(getIdentifier(), "value");
        boolean value = object.get("value").getAsBoolean();

        return new BooleanLiteral(value);
    }

    @Override
    public JsonElement serialize(BooleanLiteral booleanLiteral, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.addProperty("value", booleanLiteral.isValue());

        return result;
    }
}