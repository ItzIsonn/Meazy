package me.itzisonn_.meazy.parser.json_converter.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.InversionExpression;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class InversionExpressionConverter extends Converter<InversionExpression> {
    public InversionExpressionConverter() {
        super(RegistryIdentifier.ofDefault("inversion_expression"));
    }

    @Override
    public InversionExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        return new InversionExpression(jsonDeserializationContext.deserialize(getElement(object, "expression"), Expression.class));
    }

    @Override
    public JsonElement serialize(InversionExpression inversionExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("expression", jsonSerializationContext.serialize(inversionExpression.getExpression()));

        return result;
    }
}