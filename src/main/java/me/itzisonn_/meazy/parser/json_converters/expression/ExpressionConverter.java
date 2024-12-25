package me.itzisonn_.meazy.parser.json_converters.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.*;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.Pair;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryEntry;

import java.lang.reflect.Type;

public class ExpressionConverter implements Converter<Expression> {
    @Override
    public Expression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null) {
            for (RegistryEntry<Pair<Class<? extends Statement>, Converter<? extends Statement>>> entry : Registries.CONVERTERS.getEntries()) {
                if (Expression.class.isAssignableFrom(entry.getValue().getKey()) &&
                        object.get("type").getAsString().equals(entry.getValue().getValue().getId())) {
                    return jsonDeserializationContext.deserialize(jsonElement, entry.getValue().getKey());
                }
            }
            throw new InvalidCompiledFileException("Can't deserialize Expression because specified type is invalid");
        }

        throw new InvalidCompiledFileException("Can't deserialize Expression because specified type is null");
    }

    @Override
    public JsonElement serialize(Expression expression, Type type, JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(expression, expression.getClass());
    }

    @Override
    public String getId() {
        return "expression";
    }
}