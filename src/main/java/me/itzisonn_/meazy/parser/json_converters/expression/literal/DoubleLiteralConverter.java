package me.itzisonn_.meazy.parser.json_converters.expression.literal;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.literal.DoubleLiteral;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class DoubleLiteralConverter extends Converter<DoubleLiteral> {
    public DoubleLiteralConverter() {
        super(RegistryIdentifier.ofDefault("double_literal"));
    }

    @Override
    public DoubleLiteral deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        if (object.get("value") == null) throw new InvalidCompiledFileException(getIdentifier(), "value");
        double value = object.get("value").getAsDouble();

        return new DoubleLiteral(value);
    }

    @Override
    public JsonElement serialize(DoubleLiteral doubleLiteral, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.addProperty("value", doubleLiteral.getValue());

        return result;
    }
}