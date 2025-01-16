package me.itzisonn_.meazy.parser.json_converters.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.LogicalExpression;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class LogicalExpressionConverter extends Converter<LogicalExpression> {
    public LogicalExpressionConverter() {
        super(RegistryIdentifier.ofDefault("logical_expression"));
    }

    @Override
    public LogicalExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        if (object.get("left") == null) throw new InvalidCompiledFileException(getIdentifier(), "left");
        Expression left = jsonDeserializationContext.deserialize(object.get("left"), Expression.class);

        if (object.get("right") == null) throw new InvalidCompiledFileException(getIdentifier(), "right");
        Expression right = jsonDeserializationContext.deserialize(object.get("right"), Expression.class);

        if (object.get("operator") == null) throw new InvalidCompiledFileException(getIdentifier(), "operator");
        String operator = object.get("operator").getAsString();

        return new LogicalExpression(left, right, operator);
    }

    @Override
    public JsonElement serialize(LogicalExpression logicalExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("left", jsonSerializationContext.serialize(logicalExpression.getLeft()));
        result.add("right", jsonSerializationContext.serialize(logicalExpression.getRight()));
        result.addProperty("operator", logicalExpression.getOperator());

        return result;
    }
}