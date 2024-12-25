package me.itzisonn_.meazy.parser.json_converters.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.statement.BreakStatement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;

public class BreakStatementConverter implements Converter<BreakStatement> {
    @Override
    public BreakStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("break_statement")) {
            return new BreakStatement();
        }

        throw new InvalidCompiledFileException("Can't deserialize BreakStatement because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(BreakStatement breakStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "break_statement");

        return result;
    }

    @Override
    public String getId() {
        return "break_statement";
    }
}