package me.itzisonn_.meazy.parser.json_converters.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.AssignmentExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;

public class AssignmentExpressionConverter implements Converter<AssignmentExpression> {
    @Override
    public AssignmentExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("assignment_expression")) {
            if (object.get("id") == null) throw new InvalidCompiledFileException("AssignmentExpression doesn't have field id");
            Expression id = jsonDeserializationContext.deserialize(object.get("id"), Expression.class);

            if (object.get("value") == null) throw new InvalidCompiledFileException("AssignmentExpression doesn't have field value");
            Expression value = jsonDeserializationContext.deserialize(object.get("value"), Expression.class);

            return new AssignmentExpression(id, value);
        }

        throw new InvalidCompiledFileException("Can't deserialize AssignmentExpression because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(AssignmentExpression assignmentExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "assignment_expression");

        result.add("id", jsonSerializationContext.serialize(assignmentExpression.getId()));
        result.add("value", jsonSerializationContext.serialize(assignmentExpression.getValue()));

        return result;
    }

    @Override
    public String getId() {
        return "assignment_expression";
    }
}