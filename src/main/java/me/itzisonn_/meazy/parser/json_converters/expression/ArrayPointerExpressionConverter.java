package me.itzisonn_.meazy.parser.json_converters.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.ArrayPointerExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class ArrayPointerExpressionConverter extends Converter<ArrayPointerExpression> {
    @Override
    public ArrayPointerExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        if (object.get("object") == null) throw new InvalidCompiledFileException(getIdentifier(), "object");
        Expression objectExpression = jsonDeserializationContext.deserialize(object.get("object"), Expression.class);

        if (object.get("pos") == null) throw new InvalidCompiledFileException(getIdentifier(), "pos");
        Expression pos = jsonDeserializationContext.deserialize(object.get("pos"), Expression.class);

        return new ArrayPointerExpression(objectExpression, pos);
    }

    @Override
    public JsonElement serialize(ArrayPointerExpression arrayDeclarationExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("object", jsonSerializationContext.serialize(arrayDeclarationExpression.getObject()));
        result.add("pos", jsonSerializationContext.serialize(arrayDeclarationExpression.getPos()));

        return result;
    }

    @Override
    public RegistryIdentifier getIdentifier() {
        return RegistryIdentifier.ofDefault("array_pointer_expression");
    }
}