package me.itzisonn_.meazy.parser.json_converters.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.ArrayDeclarationExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ArrayDeclarationExpressionConverter implements Converter<ArrayDeclarationExpression> {
    @Override
    public ArrayDeclarationExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        if (object.get("type") != null && object.get("type").getAsString().equals("array_declaration_expression")) {
            if (object.get("values") == null) throw new InvalidCompiledFileException("ArrayDeclarationExpression doesn't have field values");
            ArrayList<Expression> values = new ArrayList<>();
            for (JsonElement statement : object.get("values").getAsJsonArray()) {
                values.add(jsonDeserializationContext.deserialize(statement, Expression.class));
            }

            return new ArrayDeclarationExpression(values);
        }

        throw new InvalidCompiledFileException("Can't deserialize ArrayDeclarationExpression because specified type is null or doesn't match");
    }

    @Override
    public JsonElement serialize(ArrayDeclarationExpression arrayDeclarationExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "array_declaration_expression");

        JsonArray values = new JsonArray();
        for (Statement statement : arrayDeclarationExpression.getValues()) {
            values.add(jsonSerializationContext.serialize(statement));
        }
        result.add("values", values);

        return result;
    }

    @Override
    public String getId() {
        return "array_declaration_expression";
    }
}