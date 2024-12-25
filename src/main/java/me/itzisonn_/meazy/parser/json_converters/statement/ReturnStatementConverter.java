package me.itzisonn_.meazy.parser.json_converters.statement;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.statement.ReturnStatement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;

public class ReturnStatementConverter implements Converter<ReturnStatement> {
    @Override
    public ReturnStatement deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("return_statement")) {
            Expression value = null;
            if (object.get("value") != null) {
                value = jsonDeserializationContext.deserialize(object.get("value"), Expression.class);
            }

            return new ReturnStatement(value);
        }

        throw new InvalidCompiledFileException("Can't deserialize ReturnStatement because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(ReturnStatement returnStatement, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "return_statement");

        if (returnStatement.getValue() != null) result.add("value", jsonSerializationContext.serialize(returnStatement.getValue()));

        return result;
    }

    @Override
    public String getId() {
        return "return_statement";
    }
}