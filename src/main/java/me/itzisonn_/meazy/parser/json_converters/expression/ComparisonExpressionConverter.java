package me.itzisonn_.meazy.parser.json_converters.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.ComparisonExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class ComparisonExpressionConverter extends Converter<ComparisonExpression> {
    public ComparisonExpressionConverter() {
        super(RegistryIdentifier.ofDefault("comparison_expression"));
    }

    @Override
    public ComparisonExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Expression left = jsonDeserializationContext.deserialize(getElement(object, "left"), Expression.class);
        Expression right = jsonDeserializationContext.deserialize(getElement(object, "right"), Expression.class);
        String operator = getElement(object, "operator").getAsString();

        return new ComparisonExpression(left, right, operator);
    }

    @Override
    public JsonElement serialize(ComparisonExpression comparisonExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("left", jsonSerializationContext.serialize(comparisonExpression.getLeft()));
        result.add("right", jsonSerializationContext.serialize(comparisonExpression.getRight()));
        result.addProperty("operator", comparisonExpression.getOperator());

        return result;
    }
}