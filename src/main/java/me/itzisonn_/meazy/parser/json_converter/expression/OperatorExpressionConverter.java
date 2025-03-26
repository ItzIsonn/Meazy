package me.itzisonn_.meazy.parser.json_converter.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class OperatorExpressionConverter extends Converter<OperatorExpression> {
    public OperatorExpressionConverter() {
        super(RegistryIdentifier.ofDefault("operator_expression"));
    }

    @Override
    public OperatorExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Expression left = jsonDeserializationContext.deserialize(getElement(object, "left"), Expression.class);
        Expression right = jsonDeserializationContext.deserialize(getElement(object, "right"), Expression.class);
        String operator = getElement(object, "operator").getAsString();

        return new OperatorExpression(left, right, operator);
    }

    @Override
    public JsonElement serialize(OperatorExpression operatorExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("left", jsonSerializationContext.serialize(operatorExpression.getLeft()));
        result.add("right", jsonSerializationContext.serialize(operatorExpression.getRight()));
        result.addProperty("operator", operatorExpression.getOperator().getSymbol());

        return result;
    }
}