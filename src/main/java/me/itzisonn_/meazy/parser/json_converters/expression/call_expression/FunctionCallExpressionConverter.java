package me.itzisonn_.meazy.parser.json_converters.expression.call_expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.call_expression.FunctionCallExpression;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FunctionCallExpressionConverter implements Converter<FunctionCallExpression> {
    @Override
    public FunctionCallExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("function_call_expression")) {
            if (object.get("caller") == null) throw new InvalidCompiledFileException("FunctionCallExpression doesn't have field caller");
            Expression caller = jsonDeserializationContext.deserialize(object.get("caller"), Expression.class);

            if (object.get("args") == null) throw new InvalidCompiledFileException("FunctionCallExpression doesn't have field args");
            ArrayList<Expression> args = new ArrayList<>();
            for (JsonElement arg : object.get("args").getAsJsonArray()) {
                args.add(jsonDeserializationContext.deserialize(arg, Expression.class));
            }

            return new FunctionCallExpression(caller, args);
        }

        throw new InvalidCompiledFileException("Can't deserialize FunctionCallExpression because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(FunctionCallExpression functionCallExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "function_call_expression");

        result.add("caller", jsonSerializationContext.serialize(functionCallExpression.getCaller()));

        JsonArray args = new JsonArray();
        for (Expression arg : functionCallExpression.getArgs()) {
            args.add(jsonSerializationContext.serialize(arg));
        }
        result.add("args", args);

        return result;
    }

    @Override
    public String getId() {
        return "function_call_expression";
    }
}