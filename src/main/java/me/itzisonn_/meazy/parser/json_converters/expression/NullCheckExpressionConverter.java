package me.itzisonn_.meazy.parser.json_converters.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.NullCheckExpression;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class NullCheckExpressionConverter extends Converter<NullCheckExpression> {
    public NullCheckExpressionConverter() {
        super(RegistryIdentifier.ofDefault("null_check_expression"));
    }

    @Override
    public NullCheckExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Expression checkExpression = jsonDeserializationContext.deserialize(getElement(object, "check_expression"), Expression.class);
        Expression nullExpression = jsonDeserializationContext.deserialize(getElement(object, "null_expression"), Expression.class);

        return new NullCheckExpression(checkExpression, nullExpression);
    }

    @Override
    public JsonElement serialize(NullCheckExpression nullCheckExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("check_expression", jsonSerializationContext.serialize(nullCheckExpression.getCheckExpression()));
        result.add("null_expression", jsonSerializationContext.serialize(nullCheckExpression.getNullExpression()));

        return result;
    }
}