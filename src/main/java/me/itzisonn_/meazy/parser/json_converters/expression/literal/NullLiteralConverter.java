package me.itzisonn_.meazy.parser.json_converters.expression.literal;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.literal.NullLiteral;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;

public class NullLiteralConverter implements Converter<NullLiteral> {
    @Override
    public NullLiteral deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("null_literal")) {
            return new NullLiteral();
        }

        throw new InvalidCompiledFileException("Can't deserialize NullLiteral because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(NullLiteral nullLiteral, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "null_literal");

        return result;
    }

    @Override
    public String getId() {
        return "null_literal";
    }
}