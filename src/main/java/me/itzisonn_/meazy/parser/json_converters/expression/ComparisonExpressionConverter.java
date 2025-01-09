package me.itzisonn_.meazy.parser.json_converters.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.ComparisonExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class ComparisonExpressionConverter extends Converter<ComparisonExpression> {
    @Override
    public ComparisonExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        if (object.get("left") == null) throw new InvalidCompiledFileException(getIdentifier(), "left");
        Expression left = jsonDeserializationContext.deserialize(object.get("left"), Expression.class);

        if (object.get("right") == null) throw new InvalidCompiledFileException(getIdentifier(), "right");
        Expression right = jsonDeserializationContext.deserialize(object.get("right"), Expression.class);

        if (object.get("operator") == null) throw new InvalidCompiledFileException(getIdentifier(), "operator");
        String operator = object.get("operator").getAsString();

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

    @Override
    public RegistryIdentifier getIdentifier() {
        return RegistryIdentifier.ofDefault("comparison_expression");
    }
}