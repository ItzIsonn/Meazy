package me.itzisonn_.meazy.parser.json_converters.expression.literal;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.literal.IntLiteral;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class IntLiteralConverter extends Converter<IntLiteral> {
    public IntLiteralConverter() {
        super(RegistryIdentifier.ofDefault("int_literal"));
    }

    @Override
    public IntLiteral deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        if (object.get("value") == null) throw new InvalidCompiledFileException(getIdentifier(), "value");
        int value = object.get("value").getAsInt();

        return new IntLiteral(value);
    }

    @Override
    public JsonElement serialize(IntLiteral intLiteral, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.addProperty("value", intLiteral.getValue());

        return result;
    }
}