package me.itzisonn_.meazy.parser.json_converters.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.BinaryExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;

public class BinaryExpressionConverter implements Converter<BinaryExpression> {
    @Override
    public BinaryExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("binary_expression")) {
            if (object.get("left") == null) throw new InvalidCompiledFileException("BinaryExpression doesn't have field left");
            Expression left = jsonDeserializationContext.deserialize(object.get("left"), Expression.class);

            if (object.get("right") == null) throw new InvalidCompiledFileException("BinaryExpression doesn't have field right");
            Expression right = jsonDeserializationContext.deserialize(object.get("right"), Expression.class);

            if (object.get("operator") == null) throw new InvalidCompiledFileException("BinaryExpression doesn't have field operator");
            String operator = object.get("operator").getAsString();

            return new BinaryExpression(left, right, operator);
        }

        throw new InvalidCompiledFileException("Can't deserialize BinaryExpression because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(BinaryExpression binaryExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "binary_expression");

        result.add("left", jsonSerializationContext.serialize(binaryExpression.getLeft()));
        result.add("right", jsonSerializationContext.serialize(binaryExpression.getRight()));
        result.addProperty("operator", binaryExpression.getOperator());

        return result;
    }

    @Override
    public String getId() {
        return "binary_expression";
    }
}