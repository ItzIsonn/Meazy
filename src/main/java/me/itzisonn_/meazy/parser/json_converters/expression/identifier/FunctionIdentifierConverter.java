package me.itzisonn_.meazy.parser.json_converters.expression.identifier;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FunctionIdentifierConverter implements Converter<FunctionIdentifier> {
    @Override
    public FunctionIdentifier deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("function_identifier")) {
            if (object.get("id") == null) throw new InvalidCompiledFileException("FunctionIdentifier doesn't have field id");
            String id = object.get("id").getAsString();

            if (object.get("args") == null) throw new InvalidCompiledFileException("FunctionIdentifier doesn't have field args");
            ArrayList<Expression> args = new ArrayList<>();
            for (JsonElement arg : object.get("args").getAsJsonArray()) {
                args.add(jsonDeserializationContext.deserialize(arg, Expression.class));
            }

            return new FunctionIdentifier(id, args);
        }

        throw new InvalidCompiledFileException("Can't deserialize FunctionIdentifier because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(FunctionIdentifier functionIdentifier, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "function_identifier");

        result.addProperty("id", functionIdentifier.getId());

        JsonArray args = new JsonArray();
        for (Expression arg : functionIdentifier.getArgs()) {
            args.add(jsonSerializationContext.serialize(arg));
        }
        result.add("args", args);

        return result;
    }

    @Override
    public String getId() {
        return "function_identifier";
    }
}