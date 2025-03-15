package me.itzisonn_.meazy.parser.json_converter.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.NegationExpression;
import me.itzisonn_.meazy.parser.json_converter.Converter;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class NegationExpressionConverter extends Converter<NegationExpression> {
    public NegationExpressionConverter() {
        super(RegistryIdentifier.ofDefault("negation_expression"));
    }

    @Override
    public NegationExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        return new NegationExpression(jsonDeserializationContext.deserialize(getElement(object, "expression"), Expression.class));
    }

    @Override
    public JsonElement serialize(NegationExpression negationExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("expression", jsonSerializationContext.serialize(negationExpression.getExpression()));

        return result;
    }
}