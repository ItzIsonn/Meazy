package me.itzisonn_.meazy.parser.json_converters.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.statement.ContinueStatement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;

public class ContinueStatementConverter implements Converter<ContinueStatement> {
    @Override
    public ContinueStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("continue_statement")) {
            return new ContinueStatement();
        }

        throw new InvalidCompiledFileException("Can't deserialize ContinueStatement because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(ContinueStatement continueStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "continue_statement");

        return result;
    }

    @Override
    public String getId() {
        return "continue_statement";
    }
}