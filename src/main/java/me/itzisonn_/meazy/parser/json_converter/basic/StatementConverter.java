package me.itzisonn_.meazy.parser.json_converter.basic;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.parser.json_converter.InvalidCompiledFileException;
import me.itzisonn_.registry.multiple_entry.Pair;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.registry.RegistryEntry;

import java.lang.reflect.Type;

public class StatementConverter extends Converter<Statement> {
    public StatementConverter() {
        super(Registries.getDefaultIdentifier("statement"));
    }

    @Override
    public Statement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") == null) throw new InvalidCompiledFileException("Can't deserialize Statement because specified type is null");
        for (RegistryEntry<Pair<Class<? extends Statement>, Converter<? extends Statement>>> entry : Registries.CONVERTERS.getEntries()) {
            if (object.get("type").getAsString().equals(entry.getIdentifier().toString())) {
                return jsonDeserializationContext.deserialize(jsonElement, entry.getValue().getKey());
            }
        }

        throw new InvalidCompiledFileException("Can't deserialize Statement because specified type (" + object.get("type").getAsString() + ") is invalid");
    }

    @Override
    public JsonElement serialize(Statement statement, Type type, JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(statement, statement.getClass());
    }
}