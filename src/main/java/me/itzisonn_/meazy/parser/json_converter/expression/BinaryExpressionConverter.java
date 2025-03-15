package me.itzisonn_.meazy.parser.json_converter.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.BinaryExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class BinaryExpressionConverter extends Converter<BinaryExpression> {
    public BinaryExpressionConverter() {
        super(RegistryIdentifier.ofDefault("binary_expression"));
    }

    @Override
    public BinaryExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Expression left = jsonDeserializationContext.deserialize(getElement(object, "left"), Expression.class);
        Expression right = jsonDeserializationContext.deserialize(getElement(object, "right"), Expression.class);
        String operator = getElement(object, "operator").getAsString();

        return new BinaryExpression(left, right, operator);
    }

    @Override
    public JsonElement serialize(BinaryExpression binaryExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("left", jsonSerializationContext.serialize(binaryExpression.getLeft()));
        result.add("right", jsonSerializationContext.serialize(binaryExpression.getRight()));
        result.addProperty("operator", binaryExpression.getOperator());

        return result;
    }
}