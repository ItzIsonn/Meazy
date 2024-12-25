package me.itzisonn_.meazy.parser.json_converters.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.expression.MemberExpression;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;

public class MemberExpressionConverter implements Converter<MemberExpression> {
    @Override
    public MemberExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("member_expression")) {
            if (object.get("object") == null) throw new InvalidCompiledFileException("MemberExpression doesn't have field object");
            Expression objectExpression = jsonDeserializationContext.deserialize(object.get("object"), Expression.class);

            if (object.get("field") == null) throw new InvalidCompiledFileException("MemberExpression doesn't have field field");
            Expression field = jsonDeserializationContext.deserialize(object.get("field"), Expression.class);

            return new MemberExpression(objectExpression, field);
        }

        throw new InvalidCompiledFileException("Can't deserialize MemberExpression because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(MemberExpression memberExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "member_expression");

        result.add("object", jsonSerializationContext.serialize(memberExpression.getObject()));
        result.add("field", jsonSerializationContext.serialize(memberExpression.getField()));

        return result;
    }

    @Override
    public String getId() {
        return "member_expression";
    }
}