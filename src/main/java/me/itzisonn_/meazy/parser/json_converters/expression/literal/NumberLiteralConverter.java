package me.itzisonn_.meazy.parser.json_converters.expression.literal;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.literal.NumberLiteral;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;

public class NumberLiteralConverter implements Converter<NumberLiteral> {
    @Override
    public NumberLiteral deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("number_literal")) {
            if (object.get("value") == null) throw new InvalidCompiledFileException("NumberLiteral doesn't have field value");
            double value = object.get("value").getAsDouble();

            if (object.get("is_int") == null) throw new InvalidCompiledFileException("NumberLiteral doesn't have field is_int");
            boolean isInt = object.get("is_int").getAsBoolean();

            return new NumberLiteral(value, isInt);
        }

        throw new InvalidCompiledFileException("Can't deserialize NumberLiteral because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(NumberLiteral numberLiteral, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "number_literal");

        result.addProperty("value", numberLiteral.getValue());
        result.addProperty("is_int", numberLiteral.isInt());

        return result;
    }

    @Override
    public String getId() {
        return "number_literal";
    }
}