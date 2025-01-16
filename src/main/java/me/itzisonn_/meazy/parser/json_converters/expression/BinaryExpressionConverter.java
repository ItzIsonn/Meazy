package me.itzisonn_.meazy.parser.json_converters.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.BinaryExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
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

        if (object.get("left") == null) throw new InvalidCompiledFileException(getIdentifier(), "left");
        Expression left = jsonDeserializationContext.deserialize(object.get("left"), Expression.class);

        if (object.get("right") == null) throw new InvalidCompiledFileException(getIdentifier(), "right");
        Expression right = jsonDeserializationContext.deserialize(object.get("right"), Expression.class);

        if (object.get("operator") == null) throw new InvalidCompiledFileException(getIdentifier(), "operator");
        String operator = object.get("operator").getAsString();

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