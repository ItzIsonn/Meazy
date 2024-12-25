package me.itzisonn_.meazy.parser.json_converters.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.ArrayPointerExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;

public class ArrayPointerExpressionConverter implements Converter<ArrayPointerExpression> {
    @Override
    public ArrayPointerExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("array_pointer_expression")) {
            if (object.get("object") == null) throw new InvalidCompiledFileException("ArrayPointerExpression doesn't have field object");
            Expression objectExpression = jsonDeserializationContext.deserialize(object.get("object"), Expression.class);

            if (object.get("pos") == null) throw new InvalidCompiledFileException("ArrayPointerExpression doesn't have field pos");
            Expression pos = jsonDeserializationContext.deserialize(object.get("pos"), Expression.class);

            return new ArrayPointerExpression(objectExpression, pos);
        }

        throw new InvalidCompiledFileException("Can't deserialize ArrayPointerExpression because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(ArrayPointerExpression arrayDeclarationExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "array_pointer_expression");

        result.add("object", jsonSerializationContext.serialize(arrayDeclarationExpression.getObject()));
        result.add("pos", jsonSerializationContext.serialize(arrayDeclarationExpression.getPos()));

        return result;
    }

    @Override
    public String getId() {
        return "array_pointer_expression";
    }
}