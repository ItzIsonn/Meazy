package me.itzisonn_.meazy.parser.json_converters.expression;

import com.google.gson.*;
import me.itzisonn_.meazy.parser.ast.expression.ArrayDeclarationExpression;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.json_converters.Converter;
import me.itzisonn_.meazy.parser.json_converters.InvalidCompiledFileException;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayDeclarationExpressionConverter extends Converter<ArrayDeclarationExpression> {
    @Override
    public ArrayDeclarationExpression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();
        checkType(object);

        if (object.get("values") == null) throw new InvalidCompiledFileException(getIdentifier(), "values");
        List<Expression> values = object.get("values").getAsJsonArray().asList().stream().map(value ->
                (Expression) jsonDeserializationContext.deserialize(value, Expression.class)).collect(Collectors.toList());

        return new ArrayDeclarationExpression(values);
    }

    @Override
    public JsonElement serialize(ArrayDeclarationExpression arrayDeclarationExpression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = getJsonObject();

        JsonArray values = new JsonArray();
        for (Statement statement : arrayDeclarationExpression.getValues()) {
            values.add(jsonSerializationContext.serialize(statement));
        }
        result.add("values", values);

        return result;
    }

    @Override
    public RegistryIdentifier getIdentifier() {
        return RegistryIdentifier.ofDefault("array_declaration_expression");
    }
}