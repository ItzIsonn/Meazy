package me.itzisonn_.meazy.parser.json_converters.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.IsExpression;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class IsExpressionConverter extends Converter<IsExpression> {
    public IsExpressionConverter() {
        super(RegistryIdentifier.ofDefault("is_expression"));
    }

    @Override
    public IsExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        if (object.get("value") == null) throw new InvalidCompiledFileException(getIdentifier(), "value");
        Expression value = jsonDeserializationContext.deserialize(object.get("value"), Expression.class);

        if (object.get("data_type") == null) throw new InvalidCompiledFileException(getIdentifier(), "data_type");
        String dataType = object.get("data_type").getAsString();

        return new IsExpression(value, dataType);
    }

    @Override
    public JsonElement serialize(IsExpression isExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("value", jsonSerializationContext.serialize(isExpression.getValue()));
        result.add("data_type", jsonSerializationContext.serialize(isExpression.getDataType()));

        return result;
    }
}