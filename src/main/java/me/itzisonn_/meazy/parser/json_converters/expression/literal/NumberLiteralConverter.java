package me.itzisonn_.meazy.parser.json_converters.expression.literal;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.literal.NumberLiteral;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class NumberLiteralConverter extends Converter<NumberLiteral> {
    @Override
    public NumberLiteral deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        if (object.get("value") == null) throw new InvalidCompiledFileException(getIdentifier(), "value");
        double value = object.get("value").getAsDouble();

        if (object.get("is_int") == null) throw new InvalidCompiledFileException(getIdentifier(), "is_int");
        boolean isInt = object.get("is_int").getAsBoolean();

        return new NumberLiteral(value, isInt);
    }

    @Override
    public JsonElement serialize(NumberLiteral numberLiteral, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.addProperty("value", numberLiteral.getValue());
        result.addProperty("is_int", numberLiteral.isInt());

        return result;
    }

    @Override
    public RegistryIdentifier getIdentifier() {
        return RegistryIdentifier.ofDefault("number_literal");
    }
}