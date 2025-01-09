package me.itzisonn_.meazy.parser.json_converters.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.RegistryIdentifier;
import me.itzisonn_.meazy.registry.multiple_entry.Pair;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.registry.RegistryEntry;

import java.lang.reflect.Type;

public class StatementConverter extends Converter<Statement> {
    @Override
    public Statement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null) {
            for (RegistryEntry<Pair<Class<? extends Statement>, Converter<? extends Statement>>> entry : Registries.CONVERTERS.getEntries()) {
                if (object.get("type").getAsString().equals(entry.getIdentifier().toString())) {
                    return jsonDeserializationContext.deserialize(jsonElement, entry.getValue().getKey());
                }
            }
            throw new InvalidCompiledFileException("Can't deserialize Statement because specified type (" + object.get("type").getAsString() + ") is invalid");
        }

        throw new InvalidCompiledFileException("Can't deserialize Statement because specified type is null");
    }

    @Override
    public JsonElement serialize(Statement statement, Type type, JsonSerializationContext jsonSerializationContext) {
        return jsonSerializationContext.serialize(statement, statement.getClass());
    }

    @Override
    public RegistryIdentifier getIdentifier() {
        return RegistryIdentifier.ofDefault("statement");
    }
}