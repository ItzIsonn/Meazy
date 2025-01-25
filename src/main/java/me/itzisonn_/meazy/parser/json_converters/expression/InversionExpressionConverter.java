package me.itzisonn_.meazy.parser.json_converters.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.InversionExpression;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
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

        if (object.get("expression") == null) throw new InvalidCompiledFileException(getIdentifier(), "expression");
        Expression expression = jsonDeserializationContext.deserialize(object.get("expression"), Expression.class);

        return new InversionExpression(expression);
    }

    @Override
    public JsonElement serialize(InversionExpression inversionExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("expression", jsonSerializationContext.serialize(inversionExpression.getExpression()));

        return result;
    }
}