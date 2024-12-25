package me.itzisonn_.meazy.parser.json_converters.expression.literal;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.literal.BooleanLiteral;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;

public class BooleanLiteralConverter implements Converter<BooleanLiteral> {
    @Override
    public BooleanLiteral deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("boolean_literal")) {
            if (object.get("value") == null) throw new InvalidCompiledFileException("BooleanLiteral doesn't have field value");
            boolean value = object.get("value").getAsBoolean();

            return new BooleanLiteral(value);
        }

        throw new InvalidCompiledFileException("Can't deserialize BooleanLiteral because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(BooleanLiteral booleanLiteral, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "boolean_literal");

        result.addProperty("value", booleanLiteral.isValue());

        return result;
    }

    @Override
    public String getId() {
        return "boolean_literal";
    }
}