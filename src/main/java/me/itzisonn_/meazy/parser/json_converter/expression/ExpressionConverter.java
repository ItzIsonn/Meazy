package me.itzisonn_.meazy.parser.json_converter.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.*;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.parser.json_converter.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.RegistryIdentifier;
import me.itzisonn_.meazy.registry.multiple_entry.Pair;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryEntry;

import java.lang.reflect.Type;

public class ExpressionConverter extends Converter<Expression> {
    public ExpressionConverter() {
        super(RegistryIdentifier.ofDefault("expression"));
    }

    @Override
    public Expression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") == null) throw new InvalidCompiledFileException("Can't deserialize Expression because specified type is null");
        for (RegistryEntry<Pair<Class<? extends Statement>, Converter<? extends Statement>>> entry : Registries.CONVERTERS.getEntries()) {
            if (Expression.class.isAssignableFrom(entry.getValue().getKey()) && object.get("type").getAsString().equals(entry.getIdentifier().toString())) {
                return jsonDeserializationContext.deserialize(jsonElement, entry.getValue().getKey());
            }
        }

        throw new InvalidCompiledFileException("Can't deserialize Expression because specified type (" + object.get("type").getAsString() + ") is invalid");
    }

    @Override
    public JsonElement serialize(Expression expression, Type type, JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(expression, expression.getClass());
    }
}