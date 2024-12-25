package me.itzisonn_.meazy.parser.json_converters.expression.literal;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.literal.StringLiteral;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;

public class StringLiteralConverter implements Converter<StringLiteral> {
    @Override
    public StringLiteral deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("string_literal")) {
            if (object.get("value") == null) throw new InvalidCompiledFileException("StringLiteral doesn't have field value");
            String value = object.get("value").getAsString();

            return new StringLiteral(value);
        }

        throw new InvalidCompiledFileException("Can't deserialize StringLiteral because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(StringLiteral stringLiteral, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "string_literal");

        result.addProperty("value", stringLiteral.getValue());

        return result;
    }

    @Override
    public String getId() {
        return "string_literal";
    }
}