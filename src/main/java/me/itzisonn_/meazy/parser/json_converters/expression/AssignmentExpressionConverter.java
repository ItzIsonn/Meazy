package me.itzisonn_.meazy.parser.json_converters.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.AssignmentExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;

public class AssignmentExpressionConverter extends Converter<AssignmentExpression> {
    public AssignmentExpressionConverter() {
        super(RegistryIdentifier.ofDefault("assignment_expression"));
    }

    @Override
    public AssignmentExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        Expression id = jsonDeserializationContext.deserialize(getElement(object, "id"), Expression.class);
        Expression value = jsonDeserializationContext.deserialize(getElement(object, "value"), Expression.class);

        return new AssignmentExpression(id, value);
    }

    @Override
    public JsonElement serialize(AssignmentExpression assignmentExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        result.add("id", jsonSerializationContext.serialize(assignmentExpression.getId()));
        result.add("value", jsonSerializationContext.serialize(assignmentExpression.getValue()));

        return result;
    }
}